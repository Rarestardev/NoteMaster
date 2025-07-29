package com.rarestardev.notemaster.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rarestardev.notemaster.enums.CalenderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object CalenderPreferences {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "com.rarestardev.notemaster.Calender")
    private val KEY_CALENDER = stringPreferencesKey("calendar_type")

    fun getType(context: Context): Flow<CalenderType> = context.dataStore.data.map { prefs ->
        CalenderType.valueOf(prefs[KEY_CALENDER] ?: CalenderType.GREGORIAN.name)
    }

    suspend fun saveType(context: Context, type: CalenderType) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CALENDER] = type.name
        }
    }
}