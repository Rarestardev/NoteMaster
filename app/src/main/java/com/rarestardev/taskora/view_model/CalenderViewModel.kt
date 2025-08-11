package com.rarestardev.taskora.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarestardev.taskora.enums.CalenderType
import com.rarestardev.taskora.settings.CalenderPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */

class CalenderViewModel(context: Context) : ViewModel() {
    private val _calenderType = MutableStateFlow(CalenderType.GREGORIAN)
    val calenderType: StateFlow<CalenderType> = _calenderType

    init {
        viewModelScope.launch {
            CalenderPreferences.getType(context).firstOrNull()?.let {
                _calenderType.value = it
            } ?: run {
                CalenderPreferences.saveType(context,CalenderType.GREGORIAN)
                _calenderType.value = CalenderType.GREGORIAN
            }
        }
    }

    fun toggleType(context: Context) {
        viewModelScope.launch {
            val newType = if (_calenderType.value == CalenderType.GREGORIAN)
                CalenderType.PERSIAN else CalenderType.GREGORIAN

            CalenderPreferences.saveType(context,newType)
            _calenderType.value = newType
        }
    }
}