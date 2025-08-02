package com.rarestardev.taskora.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rarestardev.taskora.dao.NoteDao
import com.rarestardev.taskora.dao.SubTaskDao
import com.rarestardev.taskora.dao.TaskItemDao
import com.rarestardev.taskora.view_model.UnifiedViewModel

@Suppress("UNCHECKED_CAST")
class UnifiedVMFactory(
    private val noteDao: NoteDao,
    private val taskDao: TaskItemDao,
    private val subTaskDao: SubTaskDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnifiedViewModel::class.java)) {
            return UnifiedViewModel(noteDao, taskDao, subTaskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}