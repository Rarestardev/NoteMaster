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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SubTaskViewModel(subTaskDao: SubTaskDao) : ViewModel() {

    private val repository = SubTaskRepository(subTaskDao)

    val subTaskList : Flow<List<SubTask>> = repository.getAllSubTask().flowOn(Dispatchers.IO)
    var subTaskItems = mutableStateListOf<SubTask>()

    fun deleteSubTask(subTask: SubTask) {
        viewModelScope.launch {
            repository.deleteSubTask(subTask)

            Log.d(Constants.APP_LOG, "Deleted sub tasks.")
        }
    }

    fun deleteSubTaskWithTaskId(taskId: Int){
        viewModelScope.launch {
            repository.deleteSubTaskWithTaskId(taskId)

            Log.d(Constants.APP_LOG, "deleteSubTaskWithTaskId")
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
            if (subTaskItems.isNotEmpty()){
                repository.insertSubTask(subTaskItems)
                Log.d(Constants.APP_LOG, "Success insert sub task.")
            }
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
}