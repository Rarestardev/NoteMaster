package com.rarestardev.taskora.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rarestardev.taskora.dao.SubTaskDao
import com.rarestardev.taskora.view_model.SubTaskViewModel

class SubTaskViewModelFactory(private val subTaskDao: SubTaskDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubTaskViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return SubTaskViewModel(subTaskDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}