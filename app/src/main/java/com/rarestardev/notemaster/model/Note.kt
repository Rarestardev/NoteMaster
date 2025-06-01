package com.rarestardev.notemaster.model

import androidx.compose.ui.text.SpanStyle
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String,
    val timestamp: String
)

data class StyledSegment(
    val start: Int,
    val end: Int,
    val style: SpanStyle
)

