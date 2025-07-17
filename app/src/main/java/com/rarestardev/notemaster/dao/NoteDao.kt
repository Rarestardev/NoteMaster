package com.rarestardev.notemaster.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rarestardev.notemaster.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY date,timeStamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Update
    suspend fun updateNoteValue(note: Note)

    @Query("SELECT EXISTS(SELECT 1 FROM notes WHERE id = :noteId)")
    suspend fun isNoteExists(noteId: Int) : Boolean
}