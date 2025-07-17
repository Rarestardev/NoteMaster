package com.rarestardev.notemaster.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rarestardev.notemaster.model.SubTask

@Dao
interface SubTaskDao {

    @Query("SELECT * FROM sub_tasks WHERE taskTitle = :taskId ORDER BY position DESC")
    suspend fun getAllSubTaskWithTaskName(taskId: String) : List<SubTask>

    @Query("SELECT * FROM sub_tasks  ORDER BY position DESC")
    suspend fun getAllSubTask() : List<SubTask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: List<SubTask>)

    @Delete
    suspend fun deleteSubTask(subTask: SubTask)

    @Query("UPDATE sub_tasks SET subChecked = :isCheck WHERE subTaskId = :id")
    suspend fun updateSubTaskCompleted(isCheck : Boolean,id: Int)
}