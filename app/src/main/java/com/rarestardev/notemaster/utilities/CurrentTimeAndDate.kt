package com.rarestardev.notemaster.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CurrentTimeAndDate {

    fun currentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(Date())
    }

    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyy/MM/dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getCurrentTimeDate(): String {
        val dateFormat = SimpleDateFormat("yyy/MM/dd - HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    @Composable
    fun getTodayDayAsString(): String {
        val today = remember { LocalDate.now() }
        val dayFormatter = remember { DateTimeFormatter.ofPattern("dd") }
        return today.format(dayFormatter)
    }

    @Composable
    fun getCurrentMonthName(): String {
        val today = remember { LocalDate.now() }
        val dayFormatter = remember { DateTimeFormatter.ofPattern("MMMM") }
        return today.format(dayFormatter)
    }

    fun alarmTimeToText(alarmTimeMillis: Long?): String {
        return alarmTimeMillis?.let {
            val formatter = SimpleDateFormat("yyyy:MM:dd - HH:mm", Locale.getDefault())
            formatter.format(Date(it))
        } ?: "NONE"
    }
}