package com.rarestardev.notemaster.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rarestardev.notemaster.settings.ThemePreferences.Companion.THEME_SETTINGS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(THEME_SETTINGS)

class ThemePreferences(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        const val THEME_SETTINGS = "com.rarestardev.notemaster.THEME_SETTINGS"
        private val THEME_SETTINGS_KEY =
            intPreferencesKey("com.rarestardev.notemaster.THEME_SETTINGS_KEY")

        const val NIGHT_MODE = 1
        const val LIGHT_MODE = 2
        const val SYSTEM_MODE = 3
    }


    val themeFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[THEME_SETTINGS_KEY] ?: SYSTEM_MODE
    }

    suspend fun saveThemeSetting(themeMode: Int) {
        dataStore.edit {
            it[THEME_SETTINGS_KEY] = themeMode
        }
    }

}