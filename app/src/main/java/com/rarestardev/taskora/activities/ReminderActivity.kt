package com.rarestardev.taskora.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.components.BannerAds
import com.rarestardev.taskora.enums.CalenderType
import com.rarestardev.taskora.enums.ReminderType
import com.rarestardev.taskora.factory.CalendarViewModelFactory
import com.rarestardev.taskora.feature.CustomText
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.utilities.ReminderController
import com.rarestardev.taskora.view_model.CalenderViewModel
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import saman.zamani.persiandate.PersianDateFormat.PersianDateNumberCharacter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.TimeZone

class ReminderActivity : BaseActivity() {

    private val calenderViewModel: CalenderViewModel by viewModels {
        CalendarViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setComposeContent {
            ReminderScreen(calenderViewModel, this@ReminderActivity)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun ReminderScreen(
    calenderViewModel: CalenderViewModel,
    activity: Activity
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.set_reminder),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                ReminderController.clearDataStore(context)
                            }
                            activity.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_activity_desc)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.background) {
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){ BannerAds() }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        ShowReminder(paddingValues, calenderViewModel, activity)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowReminder(
    paddingValues: PaddingValues,
    calenderViewModel: CalenderViewModel,
    activity: Activity
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val calenderType by calenderViewModel.calenderType.collectAsState()
    var step by remember { mutableIntStateOf(1) }

    var initialHour by remember { mutableIntStateOf(0) }
    var initialMinute by remember { mutableIntStateOf(0) }
    var initialDateMillis by remember { mutableLongStateOf(0) }

    val now = ZonedDateTime.now()
    initialHour = now.hour
    initialMinute = now.minute
    initialDateMillis = now.toInstant().toEpochMilli()

    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedType by remember { mutableStateOf<ReminderType?>(ReminderType.NONE) }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )

    val transparentColor = Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = paddingValues.calculateTopPadding(),
                start = 24.dp,
                end = 24.dp
            ),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (step) {
            1 -> {
                Text(
                    text = stringResource(R.string.set_time),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors().copy(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectorColor = MaterialTheme.colorScheme.onSecondary,
                        clockDialUnselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                        clockDialSelectedContentColor = Color.White,
                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.onSecondary,
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                Button(
                    onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        step = 2
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = stringResource(R.string.next_to_set_date),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            2 -> {
                Text(
                    text = stringResource(R.string.set_date),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                if (calenderType == CalenderType.GREGORIAN) {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.clip(MaterialTheme.shapes.medium),
                        colors = DatePickerDefaults.colors().copy(
                            containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            headlineContentColor = MaterialTheme.colorScheme.onPrimary,
                            dividerColor = MaterialTheme.colorScheme.onSecondary,
                            dateTextFieldColors = TextFieldDefaults.colors().copy(
                                unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unfocusedIndicatorColor = transparentColor,
                                focusedIndicatorColor = transparentColor,
                                focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                cursorColor = MaterialTheme.colorScheme.onSecondary,
                                focusedLeadingIconColor = transparentColor,
                                unfocusedLeadingIconColor = transparentColor
                            ),
                            weekdayContentColor = MaterialTheme.colorScheme.onPrimary,
                            todayDateBorderColor = MaterialTheme.colorScheme.onSecondary,
                            selectedDayContainerColor = MaterialTheme.colorScheme.onSecondary,
                            dayContentColor = MaterialTheme.colorScheme.onPrimary,
                            selectedDayContentColor = Color.White
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { step = 1 }) {
                            Text(
                                text = stringResource(R.string.back),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Button(
                            onClick = {
                                selectedDate =
                                    Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: 0)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()

                                step = 3
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = stringResource(R.string.next_to_set_type),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    var millis by remember { mutableLongStateOf(0L) }

                    PersianCalendarScreen(
                        onDateMillisChanged = { newMillis ->
                            millis = newMillis
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { step = 1 }) {
                            Text(
                                stringResource(R.string.back),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = {
                                selectedDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()

                                step = 3
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                stringResource(R.string.next_to_set_type),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                }
            }

            3 -> {
                Text(
                    text = stringResource(R.string.set_type),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedType == ReminderType.NOTIFICATION,
                        onClick = { selectedType = ReminderType.NOTIFICATION }
                    )

                    Text(
                        text = stringResource(R.string.notification),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.width(16.dp))
                    RadioButton(
                        selected = selectedType == ReminderType.ALARM,
                        onClick = { selectedType = ReminderType.ALARM }
                    )

                    Text(
                        text = stringResource(R.string.alarm),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { step = 2 }) {
                        Text(
                            text = stringResource(R.string.back),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Button(
                        onClick = {
                            if (selectedTime != null && selectedDate != null && selectedType != null) {
                                val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                                val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant()
                                    .toEpochMilli()

                                if (selectedType != ReminderType.NONE) {
                                    scope.launch {
                                        ReminderController.saveType(context, selectedType!!.name)
                                        ReminderController.saveTime(context, millis)
                                        activity.finish()
                                    }
                                } else {
                                    Toast.makeText(
                                        activity.applicationContext,
                                        activity.getString(R.string.please_select_alarm_type),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                Log.d(Constants.APP_LOG,"$millis")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PersianCalendarScreen(
    onDateMillisChanged: (Long) -> Unit
) {
    var selectedMonth by remember { mutableIntStateOf(PersianDate().shMonth) }
    var selectedYear by remember { mutableIntStateOf(PersianDate().shYear) }
    var selectedDay by remember { mutableIntStateOf(-1) }

    val date = PersianDate().setShYear(selectedYear).setShMonth(selectedMonth)
    val monthName = PersianDateFormat("F", PersianDateNumberCharacter.FARSI).format(date)
    val firstDay = date.setShDay(1).dayOfWeek()
    val monthLength = date.monthLength

    Column(
        Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                if (selectedMonth == 1) {
                    selectedMonth = 12; selectedYear--
                } else selectedMonth--
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_arrow_back_ios_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            CustomText(
                "$monthName - $selectedYear",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )

            IconButton(onClick = {
                if (selectedMonth == 12) {
                    selectedMonth = 1; selectedYear++
                } else selectedMonth++
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_arrow_forward_ios_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.4.dp,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("ش", "ی", "د", "س", "چ", "پ", "ج").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val dayOffset = (firstDay + 1) % 7

        val today = remember { PersianDate() }
        val isCurrentMonth = selectedMonth == today.shMonth && selectedYear == today.shYear
        val todayDay = today.shDay

        LazyVerticalGrid(columns = GridCells.Fixed(7), content = {
            items(dayOffset) {
                Box(modifier = Modifier.size(40.dp))
            }

            items(monthLength) { day ->
                val dayNumber = day + 1
                val isToday = isCurrentMonth && todayDay == dayNumber

                val isSelected = selectedDay == dayNumber

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
                        .clickable {
                            selectedDay = dayNumber
                            val selectedPersianDate = PersianDate()
                                .setShYear(selectedYear)
                                .setShMonth(selectedMonth)
                                .setShDay(dayNumber)

                            val gYear = selectedPersianDate.grgYear
                            val gMonth = selectedPersianDate.grgMonth
                            val gDay = selectedPersianDate.grgDay

                            val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"))
                            cal.set(gYear, gMonth - 1, gDay, 0, 0, 0)

                            onDateMillisChanged(cal.timeInMillis)
                        }
                        .then(
                            if (isToday)
                                Modifier
                                    .border(
                                        0.4.dp,
                                        MaterialTheme.colorScheme.onPrimary,
                                        CircleShape
                                    )
                                    .background(MaterialTheme.colorScheme.onSecondary, CircleShape)
                            else if (isSelected) {
                                Modifier
                                    .border(
                                        0.4.dp,
                                        MaterialTheme.colorScheme.onPrimary,
                                        CircleShape
                                    )
                                    .background(Color.Transparent, CircleShape)
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CustomText(
                        text = "$dayNumber",
                        style = if (isToday) MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        else MaterialTheme.typography.bodyMedium
                    )

                    if (selectedDay == -1) {
                        if (isToday) {
                            selectedDay = dayNumber
                            val selectedPersianDate = PersianDate()
                                .setShYear(selectedYear)
                                .setShMonth(selectedMonth)
                                .setShDay(dayNumber)

                            val gYear = selectedPersianDate.grgYear
                            val gMonth = selectedPersianDate.grgMonth
                            val gDay = selectedPersianDate.grgDay

                            val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"))
                            cal.set(gYear, gMonth - 1, gDay, 0, 0, 0)

                            onDateMillisChanged(cal.timeInMillis)
                        }
                    }
                }
            }
        })
    }
}