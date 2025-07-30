package com.rarestardev.notemaster.utilities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.rarestardev.notemaster.enums.ReminderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.content.edit

class ReminderController(context: Context) {

    companion object {
        private const val REMINDER_PREF_NAME = "Controller"
        const val KEY_TIME = "Time"
        const val KEY_TYPE = "Type"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(REMINDER_PREF_NAME, Context.MODE_PRIVATE)

    private val _reminderTime = MutableStateFlow(sharedPreferences.getLong(KEY_TIME, 0L))
    val reminderTime: StateFlow<Long> = _reminderTime

    private val _reminderType = MutableStateFlow(sharedPreferences.getString(KEY_TYPE, ReminderType.NONE.name) ?: ReminderType.NONE.name)
    val reminderType: StateFlow<String> = _reminderType

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            KEY_TIME -> _reminderTime.value = prefs.getLong(KEY_TIME, 0L)
            KEY_TYPE -> _reminderType.value = prefs.getString(KEY_TYPE, ReminderType.NONE.name) ?: ReminderType.NONE.name
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun saveReminderMode(millis: Long, type: String) {
        if (millis != 0L && type != ReminderType.NONE.name) {
            sharedPreferences.edit().apply {
                putLong(KEY_TIME, millis)
                putString(KEY_TYPE, type)
                apply()
            }

            Log.d(Constants.APP_LOG,"check save all method pref")
        }
    }

    fun clearAll() {
        sharedPreferences.edit { clear() }

        Log.d(Constants.APP_LOG,"check clear all method pref")

        _reminderTime.value = 0L
        _reminderType.value = ReminderType.NONE.name
    }

    fun unregister() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}