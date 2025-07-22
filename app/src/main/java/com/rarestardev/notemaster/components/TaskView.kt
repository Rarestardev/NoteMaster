package com.rarestardev.notemaster.components

import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.activities.CreateTaskActivity
import com.rarestardev.notemaster.activities.ShowAllTasksActivity
import com.rarestardev.notemaster.enums.ReminderType
import com.rarestardev.notemaster.model.Flags
import com.rarestardev.notemaster.model.ImageResource
import com.rarestardev.notemaster.model.Task
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.utilities.Constants
import com.rarestardev.notemaster.utilities.previewFakeTaskViewModel
import com.rarestardev.notemaster.utilities.previewSubTaskViewModel
import com.rarestardev.notemaster.view_model.SubTaskViewModel
import com.rarestardev.notemaster.view_model.TaskViewModel

@Preview
@Composable
private fun TaskItemPreview() {
    NoteMasterTheme {
        TaskView(previewFakeTaskViewModel(), previewSubTaskViewModel())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskView(viewModel: TaskViewModel, subTaskViewModel: SubTaskViewModel) {
    val allTasks by viewModel.taskElement.collectAsState(emptyList())
    val lazyState = rememberLazyListState()
    val taskElement = allTasks.filter { it.isComplete == false }

    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleList(stringResource(R.string.task_bottom_bar), taskElement)

        if (taskElement.isNotEmpty()) {
            LazyRow(
                state = lazyState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                if (taskElement.isNotEmpty()) {
                    itemsIndexed(taskElement.take(10)) { index, task ->
                        LazyItems(index, task, subTaskViewModel, viewModel)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompleteTaskView(viewModel: TaskViewModel, subTaskViewModel: SubTaskViewModel) {
    val allTasks by viewModel.taskElement.collectAsState(emptyList())
    val lazyState = rememberLazyListState()
    val taskElement = allTasks.filter { it.isComplete == true }

    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleList(stringResource(R.string.task_completed), taskElement)

        if (taskElement.isNotEmpty()) {
            LazyRow(
                state = lazyState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                if (taskElement.isNotEmpty()) {
                    itemsIndexed(taskElement.take(10)) { index, task ->
                        LazyItems(index, task, subTaskViewModel, viewModel)
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
private fun TitleList(title: String, taskElement: List<Task>) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 12.dp)
        )

        if (taskElement.isNotEmpty()) {
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
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItems(
    index: Int,
    task: Task,
    subTaskViewModel: SubTaskViewModel,
    viewModel: TaskViewModel
) {
    val context = LocalContext.current
    var expandedIndex by remember { mutableIntStateOf(-1) }
    val subtaskList by subTaskViewModel.subTaskList.collectAsState(emptyList())
    val subtaskFilterList =
        subtaskList.filter { it.taskId == task.id }.sortedByDescending { it.taskId }.size

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)
            .padding(end = 8.dp)
            .combinedClickable(
                onClick = {
                    val intent =
                        Intent(
                            context,
                            CreateTaskActivity::class.java
                        ).apply {
                            putExtra(Constants.STATE_TASK_ACTIVITY, true)
                            putExtra(Constants.STATE_TASK_ID_ACTIVITY, task.id)
                        }
                    context.startActivity(intent)
                },
                onLongClick = {
                    expandedIndex = index
                }
            )
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
            val (titleRef, dividerRef, flagRef, dateRef, timeRef, subTaskSizeRef, reminderTypeRef) = createRefs()

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
                text = "SubTasks : ( $subtaskFilterList )",
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

            Row(
                Modifier
                    .constrainAs(reminderTypeRef) {
                        end.linkTo(parent.end, 6.dp)
                        bottom.linkTo(parent.bottom, 6.dp)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (task.reminderType != ReminderType.NONE.name && task.reminderTime != 0L) {
                    if (task.reminderType == ReminderType.NOTIFICATION.name) {
                        NotificationView()
                    } else {
                        AlarmView()
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
                            contentDescription = task.title,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }

        if (expandedIndex == index) {
            CustomDropdownMenu(
                subTaskViewModel = subTaskViewModel,
                task = task,
                taskViewModel = viewModel,
                onDismiss = { expandedIndex = it }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDropdownMenu(
    subTaskViewModel: SubTaskViewModel,
    taskViewModel: TaskViewModel,
    onDismiss: (Int) -> Unit,
    task: Task
) {
    val menuItem = listOf(
        stringResource(R.string.delete),
        stringResource(R.string.share),
        stringResource(R.string.change_priority)
    )

    val flagItems = listOf(
        Flags(stringResource(R.string.priority_low), R.color.priority_low),
        Flags(stringResource(R.string.priority_medium), R.color.priority_medium),
        Flags(stringResource(R.string.priority_high), R.color.priority_high)
    )

    val sheetState = rememberModalBottomSheetState()
    var priorityFlagShowPage by remember { mutableStateOf(false) }

    BackHandler {
        if (priorityFlagShowPage) priorityFlagShowPage = false
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss(-1) },
        sheetState = sheetState,
        scrimColor = Color.Transparent,
        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        if (!priorityFlagShowPage) {
            BottomSheetItems(
                menuItem[0],
                ImageResource.Vector(Icons.Default.Delete)
            ) {
                subTaskViewModel.deleteSubTaskWithTaskId(task.id)
                taskViewModel.deleteTask(task)
                onDismiss(-1)
            }

            BottomSheetItems(
                menuItem[1],
                ImageResource.Vector(Icons.Default.Share)
            ) {

            }

            BottomSheetItems(
                menuItem[2],
                ImageResource.Painter(R.drawable.icons_flag)
            ) { priorityFlagShowPage = true }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { priorityFlagShowPage = false }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                flagItems.forEach { flag ->
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 6.dp,
                                bottom = 6.dp,
                                start = 12.dp,
                                end = 12.dp
                            )
                            .background(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.shapes.small
                            )
                            .clickable {
                                val flagIndex = when (flag.name) {
                                    "LOW PRIORITY" -> {
                                        0
                                    }

                                    "MEDIUM PRIORITY" -> {
                                        1
                                    }

                                    "HIGH PRIORITY" -> {
                                        2
                                    }

                                    else -> {
                                        0
                                    }
                                }

                                taskViewModel.updatePriorityFlag(flagIndex, task.id)
                                priorityFlagShowPage = false
                            }
                    ) {
                        val (iconRef, textRef) = createRefs()

                        Icon(
                            painter = painterResource(R.drawable.icons_flag),
                            contentDescription = flag.name,
                            tint = colorResource(flag.color),
                            modifier = Modifier.constrainAs(iconRef) {
                                start.linkTo(parent.start, 12.dp)
                                top.linkTo(parent.top, 12.dp)
                                bottom.linkTo(parent.bottom, 12.dp)
                            }
                        )

                        Text(
                            text = flag.name,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.constrainAs(textRef) {
                                start.linkTo(iconRef.end, 12.dp)
                                top.linkTo(iconRef.top)
                                bottom.linkTo(iconRef.bottom)
                                end.linkTo(parent.end, 12.dp)
                                width = Dimension.fillToConstraints
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomSheetItems(name: String, source: ImageResource, onClick: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 6.dp,
                bottom = 6.dp,
                start = 12.dp,
                end = 12.dp
            )
            .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
            .clickable { onClick() }
    ) {
        val (iconRef, textRef, forwardRef) = createRefs()

        when (source) {
            is ImageResource.Vector -> {
                Icon(
                    imageVector = source.vector,
                    contentDescription = name,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.constrainAs(iconRef) {
                        start.linkTo(parent.start, 12.dp)
                        top.linkTo(parent.top, 12.dp)
                        bottom.linkTo(parent.bottom, 12.dp)
                    }
                )
            }

            is ImageResource.Painter -> {
                Icon(
                    painter = painterResource(source.drawable),
                    contentDescription = name,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.constrainAs(iconRef) {
                        start.linkTo(parent.start, 12.dp)
                        top.linkTo(parent.top, 12.dp)
                        bottom.linkTo(parent.bottom, 12.dp)
                    }
                )
            }
        }

        Text(
            text = name,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp,
            modifier = Modifier.constrainAs(textRef) {
                start.linkTo(iconRef.end, 12.dp)
                top.linkTo(iconRef.top)
                bottom.linkTo(iconRef.bottom)
                end.linkTo(forwardRef.start, 12.dp)
                width = Dimension.fillToConstraints
            }
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = name,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.constrainAs(forwardRef) {
                end.linkTo(parent.end, 12.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )
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