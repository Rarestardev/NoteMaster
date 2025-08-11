package com.rarestardev.taskora.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rarestardev.taskora.dao.NoteDao
import com.rarestardev.taskora.dao.SubTaskDao
import com.rarestardev.taskora.dao.TaskItemDao
import com.rarestardev.taskora.model.Note
import com.rarestardev.taskora.model.SubTask
import com.rarestardev.taskora.model.Task
import com.rarestardev.taskora.utilities.Constants

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */
@Database(entities = [Note::class, Task::class, SubTask::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase(){

    abstract fun noteDao() : NoteDao
    abstract fun taskItemDao() : TaskItemDao
    abstract fun subTaskDao() : SubTaskDao

    companion object{
        @Volatile
        private var INSTANCE : NoteDatabase? = null

        fun getInstance(context: Context) : NoteDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    Constants.DATABASE_NAME
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}