package com.rarestardev.taskora.repository

import com.rarestardev.taskora.dao.TaskItemDao
import com.rarestardev.taskora.model.Task

class TaskItemRepository(
    private val dao: TaskItemDao
) {
    suspend fun insertTask(task: Task) = dao.insert(task)

    fun getAllTask() = dao.getAll()

    suspend fun deleteTask(task: Task) = dao.delete(task)

    suspend fun updateIsCompleteTask(isComplete: Boolean, id: Int) =
        dao.updateIsCompleteTask(isComplete, id)

    suspend fun checkIsId(taskId: Int): Boolean = dao.checkIsId(taskId)

    suspend fun updateAllTaskItem(task: Task) = dao.updateAllTaskItem(task)

    suspend fun updateFlagPriority(flag: Int, id: Int) = dao.updateFlag(flag, id)

    fun searchTasks(query: String) = dao.searchTasks(query)

    suspend fun updateReminder(reminderType: String, id: Int) =
        dao.updateReminder(reminderType, id)
}