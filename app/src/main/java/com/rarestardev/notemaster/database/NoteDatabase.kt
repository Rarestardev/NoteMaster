package com.rarestardev.notemaster.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rarestardev.notemaster.dao.NoteDao
import com.rarestardev.notemaster.dao.SubTaskDao
import com.rarestardev.notemaster.dao.TaskItemDao
import com.rarestardev.notemaster.model.Note
import com.rarestardev.notemaster.model.SubTask
import com.rarestardev.notemaster.model.Task
import com.rarestardev.notemaster.utilities.Constants

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

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}