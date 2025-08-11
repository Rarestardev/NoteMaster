package com.rarestardev.taskora.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rarestardev.taskora.model.SubTask
import kotlinx.coroutines.flow.Flow

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */
@Dao
interface SubTaskDao {

    @Query("SELECT * FROM sub_tasks  ORDER BY position DESC")
    fun getAllSubTask(): Flow<List<SubTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: List<SubTask>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subTask: SubTask)

    @Delete
    suspend fun deleteSubTask(subTask: SubTask)

    @Query("DELETE FROM sub_tasks WHERE taskId = :taskId")
    suspend fun deleteSubTaskWithTaskId(taskId: Int)

    @Query("UPDATE sub_tasks SET subChecked = :isCheck WHERE subTaskId = :subTaskId")
    suspend fun updateSubTaskCompleted(isCheck: Boolean, subTaskId: Int)
}