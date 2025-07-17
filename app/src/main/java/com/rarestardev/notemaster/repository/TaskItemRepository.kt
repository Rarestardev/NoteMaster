package com.rarestardev.notemaster.repository

import com.rarestardev.notemaster.dao.TaskItemDao
import com.rarestardev.notemaster.model.Task

class TaskItemRepository(
    private val dao: TaskItemDao
) {
    suspend fun insertTask(task: Task) = dao.insert(task)

    suspend fun getAllTask() = dao.getAll()

    suspend fun updateIsCompleteTask(isComplete: Boolean,id: Int) = dao.updateIsCompleteTask(isComplete,id)

    suspend fun checkIsTitle(taskTitle : String) : Boolean = dao.checkIsTitle(taskTitle) == true

//    suspend fun getAlarmInfo(taskId: Int) = dao.getAlarmInfo(taskId)
}