package com.rarestardev.notemaster.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rarestardev.notemaster.dao.TaskItemDao
import com.rarestardev.notemaster.view_model.TaskViewModel

class TaskViewModelFactory (private val taskDao: TaskItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}