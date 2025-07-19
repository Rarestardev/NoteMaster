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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

open class TaskViewModel(private val taskItemDao: TaskItemDao) : ViewModel() {

    private val repository = TaskItemRepository(taskItemDao)

    companion object {
        private const val BROADCAST_REQ_CODE: Int = 456
    }

    val taskElement : Flow<List<Task>> = repository.getAllTask().flowOn(Dispatchers.IO)

    fun deleteTask(task: Task){
        viewModelScope.launch {
            taskItemDao.delete(task)

            Log.d(Constants.APP_LOG, "Delete task.")
        }
    }

    fun insertTask(context: Context) {
        viewModelScope.launch {
            val currentTimeAndDate = CurrentTimeAndDate()

            val insertTask = Task(
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

            if (!checkTitleInDatabase(titleState)){
                repository.insertTask(task = insertTask)
                Log.d(Constants.APP_LOG, "Success insert task.")
            }

            sendReminderReceiver(
                context,
                reminderTime,
                reminderType
            )
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

    suspend fun checkTitleInDatabase(taskTitle: String): Boolean {
        return repository.checkIsTitle(taskTitle)
    }

    fun updateIsTaskComplete(id: Int) {
        viewModelScope.launch {
            repository.updateIsCompleteTask(true, id)
        }
    }

//    suspend fun getAlarmInfo(taskId: Int): ReminderInfo? {
//        return repository.getAlarmInfo(taskId)
//    }

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

    var selectedCategory by mutableStateOf("Category (Personal)")
    fun updateCategoryList(newCategory: String) {
        selectedCategory = "Category ($newCategory)"
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

    var isError by mutableStateOf(false)
    fun updateIsError(why: Boolean){
        isError = why
    }
}