package com.rarestardev.notemaster.model

import com.rarestardev.notemaster.enums.ReminderType

data class ReminderInfo(
    val timeMillis : Long,
    val type: ReminderType
)
