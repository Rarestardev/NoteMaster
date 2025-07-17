package com.rarestardev.notemaster.repository

import com.rarestardev.notemaster.dao.SubTaskDao
import com.rarestardev.notemaster.model.SubTask

class SubTaskRepository(private val subTaskDao: SubTaskDao) {

    suspend fun insertSubTask(subTask: List<SubTask>) = subTaskDao.insertSubTask(subTask)

    suspend fun getAllSubTaskWithTaskName(taskId: String) = subTaskDao.getAllSubTaskWithTaskName(taskId)

    suspend fun getAllSubTask() = subTaskDao.getAllSubTask()

    suspend fun deleteSubTask(subTask: SubTask) = subTaskDao.deleteSubTask(subTask)

    suspend fun updateSubTaskCompleted(check: Boolean,id : Int) = subTaskDao.updateSubTaskCompleted(check,id)
}