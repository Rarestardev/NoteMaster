package com.rarestardev.notemaster.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rarestardev.notemaster.dao.NoteDao
import com.rarestardev.notemaster.dao.SubTaskDao
import com.rarestardev.notemaster.dao.TaskItemDao
import com.rarestardev.notemaster.view_model.UnifiedViewModel

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