package com.rarestardev.taskora.utilities

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rarestardev.taskora.enums.ReminderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */

object ReminderController {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "com.rarestardev.taskora.Controller")
    private val KEY_TYPE= stringPreferencesKey("TYPE")
    private val KEY_TIME = longPreferencesKey("TIME")

    fun getTime(context: Context): Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_TIME] ?: 0L
    }

    fun getType(context: Context): Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_TYPE] ?: ReminderType.NONE.name
    }

    suspend fun saveTime(context: Context, millis: Long) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TIME] = millis
        }
    }

    suspend fun saveType(context: Context, type: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TYPE] = type
        }
    }

    suspend fun clearDataStore(context: Context){
        context.dataStore.edit { it.clear() }
    }
}