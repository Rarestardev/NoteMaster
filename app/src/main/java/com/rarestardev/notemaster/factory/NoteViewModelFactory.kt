package com.rarestardev.notemaster.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rarestardev.notemaster.dao.NoteDao
import com.rarestardev.notemaster.view_model.NoteEditorViewModel

class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteEditorViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return NoteEditorViewModel(noteDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}