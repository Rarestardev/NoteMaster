package com.rarestardev.notemaster.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rarestardev.notemaster.enums.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SettingsPreferences {

    const val APP_SETTINGS = "com.rarestardev.notemaster.APP_SETTINGS"

    private val Context.dataStore by preferencesDataStore(name = APP_SETTINGS)
    private val THEME_KEY = stringPreferencesKey("theme_mode")
    private val LANGUAGE_KEY = stringPreferencesKey("language")


    suspend fun saveLanguage(context: Context, lang: String) {
        context.dataStore.edit {
            it[LANGUAGE_KEY] = lang
        }
    }

    fun languageFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[LANGUAGE_KEY] ?: "en" }


    suspend fun saveTheme(context: Context, mode: ThemeMode) {
        context.dataStore.edit {
            it[THEME_KEY] = mode.name
        }
    }

    fun getTheme(context: Context): Flow<ThemeMode> {
        return context.dataStore.data.map {
            ThemeMode.valueOf(it[THEME_KEY] ?: ThemeMode.SYSTEM.name)
        }
    }
}