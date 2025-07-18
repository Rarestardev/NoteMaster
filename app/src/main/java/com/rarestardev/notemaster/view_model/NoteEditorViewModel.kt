package com.rarestardev.notemaster.view_model

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
import com.rarestardev.notemaster.dao.NoteDao
import com.rarestardev.notemaster.model.Note
import com.rarestardev.notemaster.utilities.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
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
            Log.i(Constants.APP_LOG, "Note is exists on database , update note")
        }
    }
}