package com.rarestardev.taskora.view_model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarestardev.taskora.R
import com.rarestardev.taskora.dao.TaskItemDao
import com.rarestardev.taskora.enums.ReminderType
import com.rarestardev.taskora.model.Task
import com.rarestardev.taskora.receiver.ReminderReceiver
import com.rarestardev.taskora.repository.TaskItemRepository
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.utilities.CurrentTimeAndDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class TaskViewModel(taskItemDao: TaskItemDao) : ViewModel() {

    private val repository = TaskItemRepository(taskItemDao)

    companion object {
        private const val BROADCAST_REQ_CODE: Int = 456
    }

    val taskElement: Flow<List<Task>> = repository.getAllTask().flowOn(Dispatchers.IO)

    val upcomingTasks: Flow<List<Task>> = taskElement
        .map { tasks ->
            val now = System.currentTimeMillis()
            tasks.filter { it.reminderTime != null && it.reminderTime > now }
        }
        .flowOn(Dispatchers.Default)

    private val _searchQuery = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val result: StateFlow<List<Task>> = _searchQuery
        .debounce(300)
        .flatMapLatest {
            if (it.isBlank()) flowOf(emptyList()) else
                repository.searchTasks(it)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)

            Log.d(Constants.APP_LOG, "Delete task.")
        }
    }

    fun insertTask(context: Context) {
        viewModelScope.launch {
            val currentTimeAndDate = CurrentTimeAndDate()
            if (titleState.isNotEmpty() && descriptionState.isNotEmpty()) {
                val insertTask = Task(
                    id = taskId,
                    isComplete = false,
                    title = titleState,
                    description = descriptionState,
                    priorityFlag = priorityFlag,
                    category = selectedCategory,
                    reminderTime = reminderTime,
                    reminderType = reminderType.name,
                    imagePath = imagePath,
                    date = currentTimeAndDate.getTodayDate(),
                    time = currentTimeAndDate.currentTime()
                )

                if (!checkIdInDatabase(taskId)) {
                    repository.insertTask(task = insertTask)
                    Log.d(Constants.APP_LOG, "Success insert task.")
                } else {
                    updateAllTask(insertTask)
                }
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.please_fill_title_and_description_fields),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun updateAllTask(task: Task) {
        viewModelScope.launch {
            repository.updateAllTaskItem(task)
            Log.d(Constants.APP_LOG, "Success update task.")
        }
    }

    fun scheduleNextReminderFromDb(context: Context) {
        viewModelScope.launch {
            upcomingTasks.collect { futureTasks ->
                val now = System.currentTimeMillis()

                val nextTask =
                    futureTasks.sortedBy { it.reminderTime }.firstOrNull() ?: return@collect

                if (nextTask.reminderTime!! < now) {
                    updateReminder(ReminderType.NONE.name,nextTask.id)
                    return@collect
                }

                scheduleReminder(
                    context = context,
                    timeInMillis = nextTask.reminderTime,
                    title = nextTask.title,
                    message = nextTask.description,
                    id = nextTask.id,
                    type = ReminderType.valueOf(nextTask.reminderType ?: "NONE")
                )
            }

            Log.d(Constants.APP_LOG, "scheduleNextReminderFromDb run.")
        }
    }

    fun scheduleReminder(
        context: Context,
        timeInMillis: Long,
        title: String,
        message: String,
        id: Int,
        type: ReminderType
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(Constants.ALARM_TYPE, type.name)
            putExtra(Constants.ALARM_MESSAGE, message)
            putExtra(Constants.ALARM_TITLE, title)
            putExtra(Constants.ALARM_ID, id)
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            BROADCAST_REQ_CODE,
            intent,
            flags
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )

        Log.d(Constants.APP_LOG, "Reminder scheduled for type: $type at $timeInMillis")
    }

    fun updateReminder(type: String, id: Int) {
        viewModelScope.launch {
            repository.updateReminder(type, id)
            Log.d(Constants.APP_LOG, "updated reminder")
        }
    }

    suspend fun checkIdInDatabase(taskId: Int): Boolean {
        return repository.checkIsId(taskId)
    }

    fun updateIsTaskComplete(id: Int, isDone: Boolean) {
        viewModelScope.launch {
            repository.updateIsCompleteTask(isDone, id)
        }
    }

    fun updatePriorityFlag(flag: Int, id: Int) {
        viewModelScope.launch {
            repository.updateFlagPriority(flag, id)
            Log.d(Constants.APP_LOG, "Updated flag success.")
        }
    }


    var taskId by mutableIntStateOf(0)
    fun updateTaskId(id: Int) {
        taskId = id
    }

    var titleState by mutableStateOf("")
    fun updateTitleFieldValue(value: String) {
        titleState = value
    }

    var descriptionState by mutableStateOf("")
    fun updateDescriptionFieldValue(value: String) {
        descriptionState = value
    }

    var priorityFlag by mutableIntStateOf(0)
    fun updatePriority(index: Int) {
        priorityFlag = index
    }

    var selectedCategory by mutableStateOf("")
    fun updateCategoryList(newCategory: String) {
        selectedCategory = newCategory
    }

    var reminderTime by mutableLongStateOf(0)
    fun updateReminderTime(time: Long) {
        reminderTime = time
    }

    var reminderType by mutableStateOf(ReminderType.NONE)
    fun updateReminderType(type: ReminderType) {
        reminderType = type
    }

    var imagePath by mutableStateOf("")
    fun updateImagePath(path: String) {
        imagePath = path
    }

    var isPreviewTask by mutableStateOf(false)
    fun updateIsPreviewTask(b: Boolean) {
        isPreviewTask = b
    }

    fun updateAllValueForEditing(task: Task) {
        updateTaskId(task.id)
        updateTitleFieldValue(task.title)
        updateDescriptionFieldValue(task.description)
        updatePriority(task.priorityFlag)
        task.category?.let { updateCategoryList(it) }
        task.reminderTime?.let { updateReminderTime(it) }

        val type = when (task.reminderType) {
            "NONE" -> ReminderType.NONE
            "NOTIFICATION" -> ReminderType.NOTIFICATION
            "ALARM" -> ReminderType.ALARM
            else -> {
                ReminderType.NONE
            }
        }
        updateReminderType(type)
        task.imagePath?.let { updateImagePath(it) }

        Log.d(Constants.APP_LOG, "Update all value on task view model")
    }
}