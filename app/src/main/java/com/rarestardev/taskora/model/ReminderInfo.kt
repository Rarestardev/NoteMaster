package com.rarestardev.taskora.model

import com.rarestardev.taskora.enums.ReminderType

data class ReminderInfo(
    val timeMillis : Long,
    val type: ReminderType
)
