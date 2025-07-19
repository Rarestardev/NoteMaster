package com.rarestardev.notemaster.repository

import com.rarestardev.notemaster.dao.SubTaskDao
import com.rarestardev.notemaster.model.SubTask
import kotlinx.coroutines.flow.Flow

class SubTaskRepository(private val subTaskDao: SubTaskDao) {

    suspend fun insertSubTask(subTask: List<SubTask>) = subTaskDao.insertSubTask(subTask)

    fun getAllSubTask() = subTaskDao.getAllSubTask()

    suspend fun deleteSubTask(subTask: SubTask) = subTaskDao.deleteSubTask(subTask)

    suspend fun deleteSubTaskWithTaskId(taskTitle: String) = subTaskDao.deleteSubTaskWithTaskId(taskTitle)

    suspend fun updateSubTaskCompleted(check: Boolean,id : Int) = subTaskDao.updateSubTaskCompleted(check,id)
}