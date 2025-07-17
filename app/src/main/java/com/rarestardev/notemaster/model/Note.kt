package com.rarestardev.notemaster.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val noteTitle: String,
    val noteText: String,
    val priority: Int,
    val timeStamp: String,
    val date: String,
    val fontWeight: Int,
    val fontSize: Float
)