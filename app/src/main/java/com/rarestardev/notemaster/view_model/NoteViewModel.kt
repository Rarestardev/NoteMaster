package com.rarestardev.notemaster.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarestardev.notemaster.dao.NoteDao
import com.rarestardev.notemaster.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    fun addNote(note: Note) {
        viewModelScope.launch {
            noteDao.insertNote(note)
        }
    }
}