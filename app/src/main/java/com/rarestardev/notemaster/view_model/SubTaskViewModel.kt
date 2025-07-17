package com.rarestardev.notemaster.view_model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarestardev.notemaster.dao.SubTaskDao
import com.rarestardev.notemaster.model.SubTask
import com.rarestardev.notemaster.repository.SubTaskRepository
import com.rarestardev.notemaster.utilities.Constants
import kotlinx.coroutines.launch

class SubTaskViewModel(private val subTaskDao: SubTaskDao) : ViewModel() {

    private val repository = SubTaskRepository(subTaskDao)

    val subTaskList = mutableStateListOf<SubTask>()

    fun loadAllSubTasksWithTaskName(taskId: String) {
        viewModelScope.launch {
            val subTasks = repository.getAllSubTaskWithTaskName(taskId)
            subTaskList.clear()
            subTaskList.addAll(subTasks)

            Log.d(Constants.APP_LOG, "Success load all sub tasks with task name.")
        }
    }

    fun loadAllSubTasks() {
        viewModelScope.launch {
            val subTasks = repository.getAllSubTask()
            subTaskList.clear()
            subTaskList.addAll(subTasks)

            Log.d(Constants.APP_LOG, "Success load all sub tasks.")
        }
    }

    fun deleteSubTask(subTask: SubTask) {
        viewModelScope.launch {
            repository.deleteSubTask(subTask)

            Log.d(Constants.APP_LOG, "Deleted sub tasks.")
        }
    }

    fun updateSubTaskIsComplete(check: Boolean, id: Int) {
        viewModelScope.launch {
            repository.updateSubTaskCompleted(check, id)

            Log.d(Constants.APP_LOG, "updateSubTaskIsComplete")
        }
    }

    fun insertSubTask() {
        viewModelScope.launch {
            repository.insertSubTask(subTaskList)
            Log.d(Constants.APP_LOG, "Success insert sub task.")
        }
    }

    var descriptionState by mutableStateOf("")
    fun updateDescriptionState(value: String) {
        descriptionState = value
    }

    var subTaskPosition by mutableIntStateOf(0)
    fun updateSubTaskPosition(position: Int) {
        subTaskPosition = position
    }

    var taskTitle by mutableStateOf("")
    fun updateTaskId(text: String) {
        taskTitle = text
    }
}