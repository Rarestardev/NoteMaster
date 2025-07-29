package com.rarestardev.notemaster.view_model

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarestardev.notemaster.dao.TaskItemDao
import com.rarestardev.notemaster.enums.ImageSize
import com.rarestardev.notemaster.enums.ReminderType
import com.rarestardev.notemaster.model.Task
import com.rarestardev.notemaster.receiver.ReminderReceiver
import com.rarestardev.notemaster.repository.TaskItemRepository
import com.rarestardev.notemaster.utilities.Constants
import com.rarestardev.notemaster.utilities.CurrentTimeAndDate
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class TaskViewModel(taskItemDao: TaskItemDao) : ViewModel() {

    private val repository = TaskItemRepository(taskItemDao)

    companion object {
        private const val BROADCAST_REQ_CODE: Int = 456
    }

    val taskElement: Flow<List<Task>> = repository.getAllTask().flowOn(Dispatchers.IO)

    private val _searchQuery = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val result: StateFlow<List<Task>> = _searchQuery
        .debounce(300)
        .flatMapLatest {
            if (it.isBlank()) flowOf(emptyList()) else
            repository.searchTasks(it)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(query: String){
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
            val insertTask = Task(
                id = taskId,
                isComplete = false,
                title = titleState,
                description = descriptionState,
                priorityFlag = priorityFlag,
                category = selectedCategory,
                reminderTime = reminderTime,
                reminderType = reminderType.name,
                imageSize = imageSize.toString(),
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

            sendReminderReceiver(
                context,
                reminderTime,
                reminderType
            )
        }
    }

    fun updateAllTask(task: Task) {
        viewModelScope.launch {
            repository.updateAllTaskItem(task)
            Log.d(Constants.APP_LOG, "Success update task.")
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun sendReminderReceiver(context: Context, time: Long, type: ReminderType) {
        if (reminderType != ReminderType.NONE && time != 0L) {
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(Constants.ALARM_TIME, time)
                putExtra(Constants.ALARM_TYPE, type)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                BROADCAST_REQ_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)

            Log.d(Constants.APP_LOG, "Success send broadcast receiver")
            Log.d(Constants.APP_LOG, "Type : $type , Time : $time")
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

    var imageSize by mutableStateOf(ImageSize.MEDIUM)
    fun updateImageSize(size: ImageSize) {
        imageSize = size
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