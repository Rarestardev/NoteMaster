package com.rarestardev.notemaster.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.enums.ReminderType
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.utilities.CurrentTimeAndDate
import com.rarestardev.notemaster.utilities.previewFakeTaskViewModel
import com.rarestardev.notemaster.view_model.TaskViewModel


@Preview
@Composable
private fun DailyTask() {
    NoteMasterTheme {
        DailyTaskProgress(Modifier, previewFakeTaskViewModel())
    }
}

@Composable
fun DailyTaskProgress(modifier: Modifier = Modifier, taskViewModel: TaskViewModel) {
    val allTask by taskViewModel.taskElement.collectAsState(emptyList())
    val filterTask = allTask.filter { it.reminderType != ReminderType.NONE.name } .sortedByDescending { it.reminderTime }

    ConstraintLayout(modifier = modifier) {
        val (dateRef, reminderLayoutRef) = createRefs()

        TodayView(
            modifier = Modifier.constrainAs(dateRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            }
        )

        ConstraintLayout(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    MaterialTheme.shapes.small
                )
                .border(
                    0.3.dp,
                    MaterialTheme.colorScheme.onSecondary,
                    MaterialTheme.shapes.small
                )
                .constrainAs(reminderLayoutRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(dateRef.end, 6.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        ) {
            val now = remember { System.currentTimeMillis() }

            val closestItem = remember(filterTask) {
                filterTask.minByOrNull { item ->
                    kotlin.math.abs(item.reminderTime!! - now)
                }
            }

            filterTask.forEach { task ->
                if (task.reminderType != ReminderType.NONE.name) {
                    val (reminderTypeRef, taskTitleRef) = createRefs()

                    if (task.reminderType == ReminderType.NOTIFICATION.name) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "",
                            tint = Color.Red,
                            modifier = Modifier.constrainAs(reminderTypeRef) {
                                start.linkTo(parent.start, 6.dp)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.icons_alarm_clock),
                            contentDescription = "",
                            tint = Color.Red,
                            modifier = Modifier.constrainAs(reminderTypeRef) {
                                start.linkTo(parent.start, 6.dp)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                        )
                    }

                    closestItem?.let {
                        Text(
                            text = it.title,
                            modifier = Modifier.constrainAs(taskTitleRef) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(reminderTypeRef.end, 8.dp)
                                end.linkTo(parent.end, 6.dp)
                                width = Dimension.fillToConstraints
                            },
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayView(modifier: Modifier = Modifier) {
    val currentTimeAndDate = CurrentTimeAndDate()
    Column(
        modifier = modifier
            .width(50.dp)
            .background(
                MaterialTheme.colorScheme.onSecondaryContainer,
                MaterialTheme.shapes.small
            )
            .border(
                0.3.dp,
                MaterialTheme.colorScheme.onSecondary,
                MaterialTheme.shapes.small
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentTimeAndDate.getTodayDayAsString(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = currentTimeAndDate.getCurrentMonthName(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}