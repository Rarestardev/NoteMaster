package com.rarestardev.notemaster.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_items")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isComplete : Boolean? = null,
    val title : String,
    val description: String,
    val priorityFlag: Int, // priority flag
    val category : String? = null, // category
    val reminderTime: Long? = null, // alarm time
    val reminderType: String? = null, // alarm type
    val imageSize: String? = null, // image size
    val imagePath: String? = null, // image size
    val date : String, // date
    val time : String // timeZone
)
