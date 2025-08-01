package com.rarestardev.taskora.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sub_tasks")
data class SubTask(
    @PrimaryKey(autoGenerate = true) val subTaskId: Int = 0,
    val subChecked : Boolean? = null,
    val subTaskDescription : String,
    val taskId : Int,
    val position: Int
)
