package com.rarestardev.notemaster.utilities


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CurrentTimeAndDate {

    fun currentTime() : String{
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(Date())
    }

    fun getTodayDate() : String{
        val dateFormat = SimpleDateFormat("yyy/MM/dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}