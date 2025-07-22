package com.rarestardev.notemaster.utilities

import androidx.compose.ui.text.font.FontWeight
import com.rarestardev.notemaster.dao.NoteDao
import com.rarestardev.notemaster.dao.SubTaskDao
import com.rarestardev.notemaster.dao.TaskItemDao
import com.rarestardev.notemaster.model.Note
import com.rarestardev.notemaster.model.SubTask
import com.rarestardev.notemaster.model.Task
import com.rarestardev.notemaster.view_model.NoteEditorViewModel
import com.rarestardev.notemaster.view_model.SubTaskViewModel
import com.rarestardev.notemaster.view_model.TaskViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun previewFakeViewModel() : NoteEditorViewModel{
    val fakeDao = object : NoteDao {
        override suspend fun insertNote(note: Note) {
            TODO("Not yet implemented")
        }

        override suspend fun deleteNote(note: Note) {
            TODO("Not yet implemented")
        }

        override fun getAllNotes(): Flow<List<Note>> = flowOf(
            listOf(
                Note(
                    noteTitle = "note1",
                    noteText = "note1",
                    priority = 2,
                    timeStamp = "0..54",
                    date = "2022",
                    fontWeight = FontWeight.Normal.weight,
                    fontSize = 14f
                ),
                Note(
                    noteTitle = "note2a",
                    noteText = "note2",
                    priority = 3,
                    timeStamp = "0..54",
                    date = "2022",
                    fontWeight = FontWeight.Normal.weight,
                    fontSize = 14f
                )
            )
        )

        override suspend fun updateNoteValue(note: Note) {
            TODO("Not yet implemented")
        }

        override suspend fun isNoteExists(noteId: Int): Boolean {
            TODO("Not yet implemented")
        }

        override suspend fun updatePriority(priority: Int, id: Int) {
            TODO("Not yet implemented")
        }
    }
    val fakeViewModel = NoteEditorViewModel(fakeDao)
    return fakeViewModel
}

fun previewFakeTaskViewModel() : TaskViewModel{
    val fakeDao = object : TaskItemDao {
        override fun getAll(): Flow<List<Task>> {
            TODO("Not yet implemented")
        }

        override suspend fun insert(item: Task) {
            TODO("Not yet implemented")
        }

        override suspend fun delete(item: Task) {
            TODO("Not yet implemented")
        }

        override suspend fun updateAllTaskItem(item: Task) {
            TODO("Not yet implemented")
        }

        override suspend fun updateIsCompleteTask(isComplete: Boolean, id: Int) {
            TODO("Not yet implemented")
        }

        override suspend fun checkIsId(taskId: Int): Boolean {
            TODO("Not yet implemented")
        }

        override suspend fun updateFlag(flag: Int, id: Int) {
            TODO("Not yet implemented")
        }
    }

    return TaskViewModel(fakeDao)
}

fun previewSubTaskViewModel() : SubTaskViewModel{
    val fakeDao = object : SubTaskDao {
        override fun getAllSubTask(): Flow<List<SubTask>> {
            TODO("Not yet implemented")
        }

        override suspend fun insertSubTask(subTask: List<SubTask>) {
            TODO("Not yet implemented")
        }

        override suspend fun deleteSubTask(subTask: SubTask) {
            TODO("Not yet implemented")
        }

        override suspend fun deleteSubTaskWithTaskId(taskId: Int) {
            TODO("Not yet implemented")
        }

        override suspend fun updateSubTaskCompleted(isCheck: Boolean, id: Int) {
            TODO("Not yet implemented")
        }

    }
    return SubTaskViewModel(fakeDao)
}