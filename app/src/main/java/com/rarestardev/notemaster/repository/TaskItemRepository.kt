package com.rarestardev.notemaster.repository

import com.rarestardev.notemaster.dao.TaskItemDao
import com.rarestardev.notemaster.model.Task

class TaskItemRepository(
    private val dao: TaskItemDao
) {
    suspend fun insertTask(task: Task) = dao.insert(task)

    fun getAllTask() = dao.getAll()

    suspend fun deleteTask(task: Task) = dao.delete(task)

    suspend fun updateIsCompleteTask(isComplete: Boolean,id: Int) = dao.updateIsCompleteTask(isComplete,id)

    suspend fun checkIsId(taskId : Int) : Boolean = dao.checkIsId(taskId)

    suspend fun updateAllTaskItem(task: Task) = dao.updateAllTaskItem(task)
}