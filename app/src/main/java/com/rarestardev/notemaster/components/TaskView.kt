package com.rarestardev.notemaster.components

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.activities.ShowAllTasksActivity
import com.rarestardev.notemaster.enums.ReminderType
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.utilities.Constants
import com.rarestardev.notemaster.utilities.previewFakeTaskViewModel
import com.rarestardev.notemaster.view_model.TaskViewModel

@Preview
@Composable
private fun TaskItemPreview() {
    NoteMasterTheme {
        TaskView(previewFakeTaskViewModel())
    }
}

@Composable
fun TaskView(viewModel: TaskViewModel) {
    val context = LocalContext.current

    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.task_bottom_bar),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 12.dp)
            )

            if (viewModel.taskElement.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.see_more),
                    style = MaterialTheme.typography.labelSmall,
                    color = colorResource(R.color.text_field_label_color),
                    modifier = Modifier
                        .clickable {
                            context.startActivity(Intent(context, ShowAllTasksActivity::class.java))
                        }
                        .padding(end = 12.dp)
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        if (viewModel.taskElement.isNotEmpty()) {
            LazyRow(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                if (viewModel.taskElement.isNotEmpty()) {
                    items(viewModel.taskElement.take(10)) { task ->
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(240.dp)
                                .padding(end = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.onSecondaryContainer,
                                    MaterialTheme.shapes.small
                                )
                                .border(
                                    0.3.dp,
                                    MaterialTheme.colorScheme.onSecondary,
                                    MaterialTheme.shapes.small
                                )
                        ) {
                            ConstraintLayout(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                val (titleRef, dividerRef, flagRef, imageRef, dateRef, timeRef, subTaskSizeRef, reminderTypeRef) = createRefs()

                                FlagView(
                                    color = task.priorityFlag,
                                    modifier = Modifier
                                        .constrainAs(flagRef) {
                                            start.linkTo(parent.start)
                                            top.linkTo(parent.top)
                                        }
                                )

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Forward",
                                    modifier = Modifier
                                        .size(20.dp)
                                        .constrainAs(createRef()) {
                                            end.linkTo(parent.end)
                                            top.linkTo(flagRef.top)
                                            bottom.linkTo(flagRef.bottom)
                                        }
                                )

                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 4.dp, end = 4.dp)
                                        .constrainAs(dividerRef) {
                                            top.linkTo(flagRef.bottom, 6.dp)
                                            start.linkTo(parent.start)
                                            end.linkTo(parent.end)
                                        },
                                    thickness = 0.3.dp,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )

                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .constrainAs(titleRef) {
                                            start.linkTo(parent.start, 6.dp)
                                            top.linkTo(dividerRef.bottom)
                                            end.linkTo(parent.end, 6.dp)
                                            bottom.linkTo(subTaskSizeRef.top)
                                            width = Dimension.fillToConstraints
                                        },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start
                                )

                                Text(
                                    text = "SubTasks : (  )",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .constrainAs(subTaskSizeRef) {
                                            start.linkTo(parent.start, 6.dp)
                                            bottom.linkTo(timeRef.top, 6.dp)
                                        }
                                )

                                Text(
                                    text = "Time : ${task.time}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .constrainAs(timeRef) {
                                            start.linkTo(parent.start, 6.dp)
                                            bottom.linkTo(dateRef.top, 6.dp)
                                        }
                                )

                                Text(
                                    text = "Date : ${task.date}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .constrainAs(dateRef) {
                                            bottom.linkTo(parent.bottom, 6.dp)
                                            start.linkTo(parent.start, 6.dp)
                                        }
                                )

                                if (task.reminderType != ReminderType.NONE.name && task.reminderTime != 0L) {
                                    if (task.reminderType == ReminderType.NOTIFICATION.name) {
                                        NotificationView(
                                            modifier = Modifier
                                                .constrainAs(reminderTypeRef) {
                                                    end.linkTo(parent.end, 6.dp)
                                                    bottom.linkTo(parent.bottom, 6.dp)
                                                }
                                        )
                                    } else {
                                        AlarmView(
                                            modifier = Modifier
                                                .constrainAs(reminderTypeRef) {
                                                    end.linkTo(parent.end, 6.dp)
                                                    bottom.linkTo(parent.bottom, 6.dp)
                                                }
                                        )
                                    }

                                    Log.d(
                                        Constants.APP_LOG,
                                        "TaskView Reminder type = ${task.reminderType}"
                                    )
                                }

                                task.imagePath?.let {
                                    if (it.isNotEmpty()) {
                                        Icon(
                                            painter = painterResource(R.drawable.icons_image),
                                            contentDescription = "image",
                                            modifier = Modifier
                                                .size(20.dp)
                                                .constrainAs(imageRef) {
                                                    bottom.linkTo(reminderTypeRef.bottom)
                                                    end.linkTo(reminderTypeRef.start, 8.dp)
                                                },
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Task list is empty ...",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(top = 50.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NotificationView(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.Notifications,
        contentDescription = stringResource(R.string.notification_desc),
        modifier = modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.onSecondary
    )
}

@Composable
private fun FlagView(modifier: Modifier = Modifier, color: Int) {
    val tint = when (color) {
        0 -> colorResource(R.color.priority_low)
        1 -> colorResource(R.color.priority_medium)
        2 -> colorResource(R.color.priority_high)
        else -> {
            colorResource(R.color.priority_low)
        }
    }

    val priority = when (color) {
        0 -> "LOW"
        1 -> "MEDIUM"
        2 -> "HIGH"
        else -> {
            "LOW"
        }
    }

    Box(
        modifier = modifier
            .width(50.dp)
            .background(tint, MaterialTheme.shapes.small),
    ) {
        Text(
            text = priority,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 2.dp,
                    bottom = 2.dp,
                    start = 4.dp,
                    end = 4.dp
                ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AlarmView(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.icon_alarm),
        contentDescription = stringResource(R.string.alarm_desc),
        modifier = modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.onSecondary
    )
}