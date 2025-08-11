package com.rarestardev.taskora.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rarestardev.taskora.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */
@Dao
interface TaskItemDao {

    @Query("SELECT * FROM task_items ORDER BY date DESC")
    fun getAll(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Task)

    @Delete
    suspend fun delete(item: Task)

    @Update
    suspend fun updateAllTaskItem(item: Task)

    @Query("UPDATE task_items SET isComplete = :isComplete WHERE id = :id")
    suspend fun updateIsCompleteTask(isComplete: Boolean, id: Int)

    @Query("SELECT EXISTS (SELECT 1 FROM task_items WHERE id =:taskId)")
    suspend fun checkIsId(taskId: Int): Boolean

    @Query("UPDATE task_items SET priorityFlag = :flag WHERE id = :id")
    suspend fun updateFlag(flag: Int, id: Int)

    @Query("SELECT * FROM task_items WHERE title LIKE '%' || :query || '%' ")
    fun searchTasks(query: String): Flow<List<Task>>

    @Query("UPDATE task_items SET reminderType = :reminderType WHERE id=:id")
    suspend fun updateReminder(reminderType: String, id: Int)
}