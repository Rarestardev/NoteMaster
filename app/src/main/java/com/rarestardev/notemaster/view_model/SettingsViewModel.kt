package com.rarestardev.notemaster.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rarestardev.notemaster.enums.ThemeMode

class SettingsViewModel : ViewModel() {

    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
    fun updateThemeMode(mode: ThemeMode){
        themeMode = mode
    }
}