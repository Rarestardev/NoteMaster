package com.rarestardev.taskora.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rarestardev.taskora.dao.TaskItemDao
import com.rarestardev.taskora.view_model.TaskViewModel

class TaskViewModelFactory (private val taskDao: TaskItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}