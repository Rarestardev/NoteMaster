package com.rarestardev.notemaster.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rarestardev.notemaster.model.SubTask
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {

    @Query("SELECT * FROM sub_tasks  ORDER BY position DESC")
    fun getAllSubTask(): Flow<List<SubTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: List<SubTask>)

    @Delete
    suspend fun deleteSubTask(subTask: SubTask)

    @Query("DELETE FROM sub_tasks WHERE taskId = :taskId")
    suspend fun deleteSubTaskWithTaskId(taskId: Int)

    @Query("UPDATE sub_tasks SET subChecked = :isCheck WHERE subTaskId = :subTaskId")
    suspend fun updateSubTaskCompleted(isCheck: Boolean, subTaskId: Int)
}