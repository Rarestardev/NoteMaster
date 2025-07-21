package com.rarestardev.notemaster.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.jakewharton.threetenabp.AndroidThreeTen
import com.rarestardev.notemaster.settings.ThemePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        val themePreferences = ThemePreferences(this)

        CoroutineScope(Dispatchers.IO).launch {
            themePreferences.themeFlow.collect { themeMode ->
                when(themeMode){
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    2 -> AppCompatDelegate.MODE_NIGHT_NO
                    3 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }
        }
    }
}