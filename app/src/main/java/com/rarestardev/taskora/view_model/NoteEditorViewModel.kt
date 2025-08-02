package com.rarestardev.taskora.view_model

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarestardev.taskora.dao.NoteDao
import com.rarestardev.taskora.model.Note
import com.rarestardev.taskora.utilities.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing text editing and styling operations.
 *
 * @author Rarestardev
 *
 * 2025/06/08  JUN
 */
@SuppressLint("MutableCollectionMutableState", "AutoboxingStateCreation")
open class NoteEditorViewModel(private val noteDao: NoteDao) : ViewModel() {

    val allNote: Flow<List<Note>> = noteDao.getAllNotes().flowOn(Dispatchers.IO)

    private val _searchQuery = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val result: StateFlow<List<Note>> = _searchQuery
        .debounce(300)
        .flatMapLatest {
            if (it.isBlank()) flowOf(emptyList()) else
                noteDao.searchNotes(it)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(query: String){
        _searchQuery.value = query
    }

    var showMoreFeatureMenu by mutableStateOf(false)
    fun updateShowMoreFeatureMenu(value: Boolean) {
        showMoreFeatureMenu = value
    }

    var showMoreFontWeight by mutableStateOf(false)
    fun updateShowMoreFontWeight(value: Boolean) {
        showMoreFontWeight = value
    }

    var fontWeight by mutableStateOf(FontWeight.Normal)
    fun updateFontWeight(value: FontWeight) {
        fontWeight = value
    }

    var showMoreFontSize by mutableStateOf(false)
    fun updateShowMoreFontSize(value: Boolean) {
        showMoreFontSize = value
    }

    var fontSize by mutableFloatStateOf(14f)
    fun updateFontSize(value: Float) {
        fontSize = value
    }

    var noteTextFieldState by mutableStateOf("")
    fun updateNoteTextFieldState(value: String) {
        noteTextFieldState = value
    }

    var titleTextFieldState by mutableStateOf("")
    fun updateTitleTextFieldState(value: String) {
        titleTextFieldState = value
    }

    var priorityFlag by mutableIntStateOf(0)
    fun updatePriority(value: Int) {
        priorityFlag = value
    }

    var isEditing by mutableStateOf(false)
    fun updateIsEditing(value: Boolean) {
        isEditing = value
    }

    var noteId by mutableIntStateOf(0)
    fun updateId(value: Int) {
        noteId = value
    }

    fun updateAllValue(
        noteId: Int,
        titleTextFieldState: String,
        noteTextFieldState: String,
        priorityFlag: Int,
        fontWeight: Int,
        fontSize: Float
    ) {
        updateId(noteId)
        updatePriority(priorityFlag)
        updateTitleTextFieldState(titleTextFieldState)
        updateNoteTextFieldState(noteTextFieldState)
        updateFontWeight(FontWeight(fontWeight))
        updateFontSize(fontSize)
    }

    suspend fun checkNoteExists(noteId: Int): Boolean {
        return noteDao.isNoteExists(noteId)
    }

    fun saveNoteInDatabase(context: Context, timeStamp: String, date: String) {
        if (titleTextFieldState.isEmpty()) updateTitleTextFieldState(noteTextFieldState)
        val activity = context as? Activity

        if (noteTextFieldState.isNotEmpty()) {

            viewModelScope.launch {
                if (checkNoteExists(noteId)) {
                    updateNote(timeStamp, date)
                } else {
                    insertNote(timeStamp, date)
                }
            }

            activity?.finish()
        } else {
            activity?.finish()
        }
    }

    private fun insertNote(timeStamp: String, date: String) {
        viewModelScope.launch {
            val notes = Note(
                noteTitle = titleTextFieldState,
                noteText = noteTextFieldState,
                priority = priorityFlag,
                timeStamp = timeStamp,
                date = date,
                fontWeight = fontWeight.weight,
                fontSize = fontSize
            )

            noteDao.insertNote(notes)
            Log.d(Constants.APP_LOG, "Note is not exists on database , insert note")
        }
    }

    private fun updateNote(timeStamp: String, date: String) {
        viewModelScope.launch {
            val notes = Note(
                id = noteId,
                noteTitle = titleTextFieldState,
                noteText = noteTextFieldState,
                priority = priorityFlag,
                timeStamp = timeStamp,
                date = date,
                fontWeight = fontWeight.weight,
                fontSize = fontSize
            )

            noteDao.updateNoteValue(notes)
            Log.d(Constants.APP_LOG, "Note is exists on database , update note")
        }
    }

    fun updatePriorityFlagInDatabase(flag: Int, id: Int) {
        viewModelScope.launch {
            noteDao.updatePriority(flag,id)
            Log.d(Constants.APP_LOG, "Note is exists on database , update Priority")
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch {
            noteDao.deleteNote(note)

            Log.d(Constants.APP_LOG, "delete note on database")
        }
    }
}