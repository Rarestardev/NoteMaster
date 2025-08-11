package com.rarestardev.taskora.repository

import com.rarestardev.taskora.dao.SubTaskDao
import com.rarestardev.taskora.model.SubTask

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */

class SubTaskRepository(private val subTaskDao: SubTaskDao) {

    suspend fun insertSubTask(subTask: List<SubTask>) = subTaskDao.insertSubTask(subTask)

    fun getAllSubTask() = subTaskDao.getAllSubTask()

    suspend fun deleteSubTask(subTask: SubTask) = subTaskDao.deleteSubTask(subTask)

    suspend fun deleteSubTaskWithTaskId(taskId: Int) = subTaskDao.deleteSubTaskWithTaskId(taskId)

    suspend fun updateSubTaskCompleted(check: Boolean,id : Int) = subTaskDao.updateSubTaskCompleted(check,id)
}