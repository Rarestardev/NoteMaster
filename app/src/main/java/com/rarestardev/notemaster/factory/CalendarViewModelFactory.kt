package com.rarestardev.notemaster.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rarestardev.notemaster.view_model.CalenderViewModel

@Suppress("UNCHECKED_CAST")
class CalendarViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalenderViewModel(context.applicationContext) as T
    }
}