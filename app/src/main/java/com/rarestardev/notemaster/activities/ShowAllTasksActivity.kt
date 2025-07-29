package com.rarestardev.notemaster.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.database.NoteDatabase
import com.rarestardev.notemaster.enums.ReminderType
import com.rarestardev.notemaster.factory.SubTaskViewModelFactory
import com.rarestardev.notemaster.factory.TaskViewModelFactory
import com.rarestardev.notemaster.feature.GlideImage
import com.rarestardev.notemaster.model.Flags
import com.rarestardev.notemaster.model.ImageResource
import com.rarestardev.notemaster.model.Task
import com.rarestardev.notemaster.utilities.Constants
import com.rarestardev.notemaster.view_model.SubTaskViewModel
import com.rarestardev.notemaster.view_model.TaskViewModel

class ShowAllTasksActivity : BaseActivity() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(NoteDatabase.getInstance(this).taskItemDao())
    }

    private val subTaskViewModel: SubTaskViewModel by viewModels {
        SubTaskViewModelFactory(NoteDatabase.getInstance(this).subTaskDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val state = intent.getBooleanExtra(Constants.STATE_TASK_PRIORITY_ACTIVITY, false)
        val isCompleteTask =
            intent.getBooleanExtra(Constants.STATE_TASK_IS_COMPLETE_ACTIVITY, false)
        val category = intent.getStringExtra("Category") ?: ""
        Log.d(Constants.APP_LOG, category)

        setComposeContent {
            ShowAllTasksActivityScreen(
                taskViewModel,
                category,
                state,
                subTaskViewModel,
                isCompleteTask
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun ShowAllTasksActivityScreen(
    taskViewModel: TaskViewModel,
    category: String,
    priority: Boolean,
    subTaskViewModel: SubTaskViewModel,
    isCompleteTask: Boolean
) {
    val allTask by taskViewModel.taskElement.collectAsState(emptyList())
    val activity = LocalContext.current as? Activity
    val lazyState = rememberLazyListState()
    val context = LocalContext.current
    var titleActivity by remember { mutableStateOf("") }

    if (titleActivity.isEmpty()) titleActivity = stringResource(R.string.task_bottom_bar)

    val filteredTask = if (!isCompleteTask) {
        if (priority) {
            allTask.filter { it.priorityFlag == 2 }.sortedByDescending { it.priorityFlag }
        } else {
            if (category.isEmpty()) {
                allTask
            } else {
                allTask.filter { it.category == category }.sortedByDescending { it.category }
            }
        }
    } else {
        allTask.filter { it.isComplete == isCompleteTask }.sortedByDescending { it.isComplete }
    }

    titleActivity = if (!isCompleteTask) {
        if (priority) {
            stringResource(R.string.priority_high)
        } else {
            if (category.isEmpty()) {
                stringResource(R.string.task_bottom_bar)
            } else {
                category
            }
        }
    } else {
        "Completed tasks"
    }

    var isShowBottomSheetMenu by remember { mutableStateOf(false) }
    var currentTaskItem by remember { mutableStateOf(Task()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = titleActivity,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { activity?.finish() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_activity_desc),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (filteredTask.isNotEmpty()) {
            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding() + 12.dp,
                        start = 12.dp,
                        end = 12.dp,
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
                items(filteredTask) {
                    val subtaskList by subTaskViewModel.subTaskList.collectAsState(emptyList())
                    val subtaskFilterList = subtaskList.filter { filter -> filter.taskId == it.id }
                    val subtaskFilterListIsComplete =
                        subtaskFilterList.filter { it.subChecked == true }.size

                    val progress =
                        if (subtaskFilterList.isNotEmpty()) subtaskFilterListIsComplete.toFloat() / subtaskFilterList.size else 0.00f
                    val percentage = (progress * 100).toInt()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(bottom = 8.dp)
                            .combinedClickable(
                                onClick = {
                                    val intent =
                                        Intent(
                                            context,
                                            CreateTaskActivity::class.java
                                        ).apply {
                                            putExtra(Constants.STATE_TASK_ACTIVITY, true)
                                            putExtra(Constants.STATE_TASK_ID_ACTIVITY, it.id)
                                        }
                                    context.startActivity(intent)
                                },
                                onLongClick = {
                                    isShowBottomSheetMenu = true
                                    currentTaskItem = it
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
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val (dateLayoutRef, verticalDividerRef, allDesignRef) = createRefs()

                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(60.dp)
                                    .padding(6.dp)
                                    .constrainAs(dateLayoutRef) {
                                        start.linkTo(parent.start)
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                        height = Dimension.fillToConstraints
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Create on",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = colorResource(R.color.drawer_text_icon_color),
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = it.time,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = colorResource(R.color.drawer_text_icon_color)
                                )

                                Text(
                                    text = it.date,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = colorResource(R.color.drawer_text_icon_color)
                                )

                                Spacer(Modifier.height(10.dp))

                                it.imagePath?.let {
                                    if (it.isNotEmpty()) {
                                        GlideImage(
                                            imageUrl = it,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp)
                                                .clip(MaterialTheme.shapes.small)
                                        )
                                    }
                                }
                            }

                            VerticalDivider(
                                modifier = Modifier.constrainAs(verticalDividerRef) {
                                    start.linkTo(dateLayoutRef.end)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    height = Dimension.fillToConstraints
                                },
                                thickness = 0.3.dp,
                                color = MaterialTheme.colorScheme.onSecondary
                            )

                            Column(
                                modifier = Modifier
                                    .constrainAs(allDesignRef) {
                                        start.linkTo(verticalDividerRef.end)
                                        top.linkTo(parent.top)
                                        end.linkTo(parent.end)
                                        bottom.linkTo(parent.bottom)
                                        width = Dimension.fillToConstraints
                                        height = Dimension.fillToConstraints
                                    }
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = it.title,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = it.description,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    maxLines = 3,
                                    textAlign = TextAlign.Start,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 12.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (it.reminderType != ReminderType.NONE.name && it.reminderTime != 0L) {
                                        if (it.reminderType == ReminderType.NOTIFICATION.name) {
                                            NotificationView()
                                        } else {
                                            AlarmView()
                                        }

                                        Log.d(
                                            Constants.APP_LOG,
                                            "TaskView Reminder type = ${it.reminderType}"
                                        )
                                    }

                                    FlagView(
                                        color = it.priorityFlag
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 0.3.dp,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )

                                ConstraintLayout(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val (progressBarRef, percentageRef) = createRefs()

                                    LinearProgressIndicator(
                                        progress = { progress.coerceIn(0f..1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .constrainAs(progressBarRef) {
                                                start.linkTo(parent.start)
                                                top.linkTo(parent.top)
                                                bottom.linkTo(parent.bottom)
                                                end.linkTo(percentageRef.start, 6.dp)
                                                width = Dimension.fillToConstraints
                                            },
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        trackColor = MaterialTheme.colorScheme.background,
                                        gapSize = 0.dp
                                    )

                                    Text(
                                        text = "$percentage %",
                                        modifier = Modifier.constrainAs(percentageRef) {
                                            end.linkTo(parent.end)
                                            top.linkTo(progressBarRef.top)
                                            bottom.linkTo(progressBarRef.bottom)
                                        },
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isShowBottomSheetMenu) {
                CustomDropdownMenu(
                    subTaskViewModel = subTaskViewModel,
                    task = currentTaskItem,
                    taskViewModel = taskViewModel,
                    onDismiss = { isShowBottomSheetMenu = it }
                )
            }
        }
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDropdownMenu(
    subTaskViewModel: SubTaskViewModel,
    taskViewModel: TaskViewModel,
    onDismiss: (Boolean) -> Unit,
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
        onDismissRequest = { onDismiss(false) },
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
                onDismiss(false)
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
private fun AlarmView(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.icon_alarm),
        contentDescription = stringResource(R.string.alarm_desc),
        modifier = modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.onSecondary
    )
}
