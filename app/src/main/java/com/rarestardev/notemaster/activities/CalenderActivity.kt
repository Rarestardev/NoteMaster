package com.rarestardev.notemaster.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.database.NoteDatabase
import com.rarestardev.notemaster.enums.CalenderType
import com.rarestardev.notemaster.enums.ReminderType
import com.rarestardev.notemaster.factory.CalendarViewModelFactory
import com.rarestardev.notemaster.factory.TaskViewModelFactory
import com.rarestardev.notemaster.utilities.Constants
import com.rarestardev.notemaster.view_model.CalenderViewModel
import com.rarestardev.notemaster.view_model.TaskViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import saman.zamani.persiandate.PersianDateFormat.PersianDateNumberCharacter
import java.util.Calendar
import java.util.Locale

class CalenderActivity : BaseActivity() {

    private val calenderViewModel: CalenderViewModel by viewModels {
        CalendarViewModelFactory(applicationContext)
    }

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(NoteDatabase.getInstance(this).taskItemDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setComposeContent {
            CalenderScreen(calenderViewModel, taskViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun CalenderScreen(viewModel: CalenderViewModel, taskViewModel: TaskViewModel) {
    val calenderType by viewModel.calenderType.collectAsState()
    val activity = LocalContext.current as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.calender),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            activity?.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_activity_desc)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    top = paddingValues.calculateTopPadding() + 12.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = paddingValues.calculateBottomPadding() + 12.dp
                )
        ) {
            when (calenderType) {
                CalenderType.GREGORIAN -> GregorianCalendarScreen(taskViewModel)
                CalenderType.PERSIAN -> PersianCalendarScreen(taskViewModel)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GregorianCalendarScreen(taskViewModel: TaskViewModel) {
    var selectedDay by remember { mutableIntStateOf(-1) }
    var millis by remember { mutableLongStateOf(0L) }

    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val monthText = currentDate.month.getDisplayName(
        org.threeten.bp.format.TextStyle.FULL_STANDALONE,
        Locale.getDefault()
    )
    val yearMonth = YearMonth.of(currentDate.year, currentDate.month)
    val firstDayOfWeek = (yearMonth.atDay(1).dayOfWeek.value % 7)
    val daysInMonth = yearMonth.lengthOfMonth()

    Column(
        Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        // Header with navigation
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { currentDate = currentDate.minusMonths(1) }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_arrow_back_ios_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                "$monthText ${currentDate.year}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            IconButton(onClick = { currentDate = currentDate.plusMonths(1) }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_arrow_forward_ios_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        MyDivider()

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val today = LocalDate.now()

        LazyVerticalGrid(columns = GridCells.Fixed(7), content = {
            items(firstDayOfWeek) {
                Box(modifier = Modifier.size(40.dp))
            }
            items(daysInMonth) { dayIndex ->
                val dayNumber = dayIndex + 1
                val isToday =
                    currentDate.year == today.year &&
                            currentDate.month == today.month &&
                            dayNumber == today.dayOfMonth

                val isSelected = selectedDay == dayNumber

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
                        .clickable {
                            selectedDay = dayNumber
                            val clickedDate =
                                LocalDate.of(currentDate.year, currentDate.monthValue, dayNumber)
                            millis =
                                clickedDate.atStartOfDay(org.threeten.bp.ZoneId.systemDefault())
                                    .toInstant().toEpochMilli()
                        }
                        .then(
                            if (isToday) {
                                Modifier
                                    .border(
                                        0.4.dp,
                                        MaterialTheme.colorScheme.onPrimary,
                                        CircleShape
                                    )
                                    .background(MaterialTheme.colorScheme.onSecondary, CircleShape)
                            } else if (isSelected) {
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
                    Text(
                        text = "$dayNumber",
                        style = if (isToday) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        else MaterialTheme.typography.bodyMedium,
                        color = if (isToday) Color.White else MaterialTheme.colorScheme.onPrimary
                    )

                    if (selectedDay == -1) {
                        if (isToday) {
                            selectedDay = dayNumber
                            val clickedDate =
                                LocalDate.of(currentDate.year, currentDate.monthValue, dayNumber)
                            millis =
                                clickedDate.atStartOfDay(org.threeten.bp.ZoneId.systemDefault())
                                    .toInstant().toEpochMilli()
                        }
                    }
                }
            }
        })

        Spacer(Modifier.height(12.dp))

        MyDivider()

        Spacer(Modifier.height(12.dp))

        TaskItemInCalender(taskViewModel, millis)
    }
}

@Composable
private fun PersianCalendarScreen(taskViewModel: TaskViewModel) {
    var selectedMonth by remember { mutableIntStateOf(PersianDate().shMonth) }
    var selectedYear by remember { mutableIntStateOf(PersianDate().shYear) }
    var selectedDay by remember { mutableIntStateOf(-1) }
    var millis by remember { mutableLongStateOf(0L) }

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

            Text(
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

        MyDivider()

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

                            val cal = Calendar.getInstance()
                            cal.set(gYear, gMonth - 1, gDay, 0, 0, 0)

                            millis = cal.timeInMillis
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
                    Text(
                        text = "$dayNumber",
                        style = if (isToday) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
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

                            val cal = Calendar.getInstance()
                            cal.set(gYear, gMonth - 1, gDay, 0, 0, 0)

                            millis = cal.timeInMillis
                        }
                    }
                }
            }
        })

        Spacer(Modifier.height(12.dp))

        MyDivider()

        Spacer(Modifier.height(12.dp))

        TaskItemInCalender(taskViewModel, millis)
    }
}

@Composable
private fun MyDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 0.4.dp,
        color = MaterialTheme.colorScheme.onSecondary
    )
}

@Composable
private fun TaskItemInCalender(taskViewModel: TaskViewModel, today: Long) {
    val allTask by taskViewModel.taskElement.collectAsState(emptyList())
    val context = LocalContext.current
    val castTodayToString = extractGregorianDateFromMillis(today)
    val filterTask = allTask
        .filter { it.reminderType != ReminderType.NONE.name }
        .filter { extractGregorianDateFromMillis(it.reminderTime) == castTodayToString }
        .sortedByDescending { it.id }

    Text(
        text = stringResource(R.string.task_bottom_bar),
        modifier = Modifier.fillMaxWidth(),
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = MaterialTheme.colorScheme.onPrimary
    )

    Text(
        text = "${filterTask.size} tasks for today.",
        modifier = Modifier.fillMaxWidth(),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = colorResource(R.color.text_field_label_color)
    )

    Spacer(Modifier.height(10.dp))

    if (filterTask.isNotEmpty()) {
        filterTask.forEach {
            if (extractGregorianDateFromMillis(it.reminderTime) == castTodayToString) {
                val priorityColor = when (it.priorityFlag) {
                    0 -> colorResource(R.color.priority_low)
                    1 -> colorResource(R.color.priority_medium)
                    2 -> colorResource(R.color.priority_high)
                    else -> {
                        colorResource(R.color.priority_low)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = it.time + "\n Time ",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.drawer_text_icon_color),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(priorityColor, CircleShape)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.onSecondaryContainer,
                                MaterialTheme.shapes.medium
                            )
                            .border(0.5.dp, priorityColor, MaterialTheme.shapes.medium)
                            .clickable {
                                val intent = Intent(context, CreateTaskActivity::class.java).apply {
                                    putExtra(Constants.STATE_TASK_ID_ACTIVITY, it.id)
                                }

                                context.startActivity(intent)
                            },
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = it.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    top = 8.dp
                                ),
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            thickness = 0.2.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )

                        Text(
                            text = it.description,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp
                                ),
                            color = MaterialTheme.colorScheme.onPrimary,
                            minLines = 2,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            thickness = 0.2.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )

                        Text(
                            text = it.time + " - " + it.date,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Start)
                                .padding(
                                    start = 10.dp,
                                    bottom = 8.dp
                                ),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Start,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

            } else {
                NothingTaskText()
            }
        }
    } else {
        NothingTaskText()
    }
}

@Composable
private fun NothingTaskText() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.there_is_nothing_today),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

private fun extractGregorianDateFromMillis(millis: Long?): String {
    val instant = org.threeten.bp.Instant.ofEpochMilli(millis!!)
    val localDate =
        org.threeten.bp.LocalDateTime.ofInstant(instant, org.threeten.bp.ZoneId.systemDefault())
    val formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return formatter.format(localDate)
}