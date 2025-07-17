package com.rarestardev.notemaster.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rarestardev.notemaster.model.ReminderInfo
import com.rarestardev.notemaster.model.Task

@Dao
interface TaskItemDao {

    @Query("SELECT * FROM task_items ORDER BY date,time ASC")
    suspend fun getAll(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Task)

    @Update
    suspend fun updateAllTaskItem(item: Task)

    @Query("UPDATE task_items SET isComplete = :isComplete WHERE id = :id")
    suspend fun updateIsCompleteTask(isComplete: Boolean,id: Int)

    @Query("SELECT title FROM task_items WHERE title = :taskTitle LIMIT 1")
    suspend fun checkIsTitle(taskTitle: String) : Boolean? = false

//    @Query("SELECT reminderTime,reminderType FROM task_items WHERE id = :taskId")
//    suspend fun getAlarmInfo(taskId: Int): ReminderInfo
}