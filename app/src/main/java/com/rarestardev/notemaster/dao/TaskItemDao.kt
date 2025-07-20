package com.rarestardev.notemaster.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rarestardev.notemaster.model.Task
import kotlinx.coroutines.flow.Flow

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
}