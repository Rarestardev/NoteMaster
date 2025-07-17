package com.rarestardev.notemaster.feature

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rarestardev.notemaster.enums.ReminderType
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderBottomSheet(
    onSet: (Long, ReminderType) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var step by remember { mutableIntStateOf(1) }

    val nowCalendar = Calendar.getInstance()
    val initialHour = nowCalendar.get(Calendar.HOUR_OF_DAY)
    val initialMinute = nowCalendar.get(Calendar.MINUTE)
    val initialDate = nowCalendar.timeInMillis

    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedType by remember { mutableStateOf<ReminderType?>(ReminderType.ALARM) }

    val timePickState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Set Reminder",
                style = MaterialTheme.typography.titleLarge
            )

            when (step) {
                1 -> {
                    Text(text = "Set Time", style = MaterialTheme.typography.labelLarge)

                    TimePicker(state = timePickState)
                    Button(onClick = {
                        selectedTime = LocalTime.of(timePickState.hour, timePickState.minute)
                        step = 2
                    }) {
                        Text(text = "Next to set date")
                    }
                }

                2 -> {
                    Text(text = "Set Date", style = MaterialTheme.typography.labelLarge)

                    DatePicker(state = datePickerState)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { step = 1 }) {
                            Text(text = "Back")
                        }
                        Button(onClick = {
                            selectedDate =
                                Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: 0)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            step = 3
                        }) {
                            Text(text = "Next to set type")
                        }
                    }
                }

                3 -> {
                    Text(text = "Set Reminder", style = MaterialTheme.typography.labelLarge)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedType == ReminderType.NOTIFICATION,
                            onClick = { selectedType == ReminderType.NOTIFICATION }
                        )

                        Text(text = "Notification")

                        Spacer(Modifier.width(16.dp))
                        RadioButton(
                            selected = selectedType == ReminderType.ALARM,
                            onClick = { selectedType == ReminderType.ALARM }
                        )

                        Text(text = "Alarm")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { step = 2 }) {
                            Text(text = "Back")
                        }
                        Button(onClick = {
                            if (selectedTime != null && selectedDate != null && selectedType != null) {
                                val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                                val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant()
                                    .toEpochMilli()
                                onSet(millis, selectedType!!)
                                scope.launch { sheetState.hide() }
                                    .invokeOnCompletion { onDismiss() }
                            }
                        }) {
                            Text(text = "Save", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}