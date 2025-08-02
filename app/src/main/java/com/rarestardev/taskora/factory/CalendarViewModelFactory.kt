package com.rarestardev.taskora.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rarestardev.taskora.view_model.CalenderViewModel

@Suppress("UNCHECKED_CAST")
class CalendarViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalenderViewModel(context.applicationContext) as T
    }
}