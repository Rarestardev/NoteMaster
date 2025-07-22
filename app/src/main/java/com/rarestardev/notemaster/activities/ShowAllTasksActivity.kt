package com.rarestardev.notemaster.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.rarestardev.notemaster.model.Flags
import com.rarestardev.notemaster.model.ImageResource
import com.rarestardev.notemaster.model.Task
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.utilities.Constants
import com.rarestardev.notemaster.view_model.SubTaskViewModel
import com.rarestardev.notemaster.view_model.TaskViewModel

class ShowAllTasksActivity : ComponentActivity() {

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

        setContent {
            NoteMasterTheme {
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
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .combinedClickable(
                                onClick = {
                                    val intent =
                                        Intent(context, CreateTaskActivity::class.java).apply {
                                            putExtra(Constants.STATE_TASK_ACTIVITY, true)
                                            putExtra(Constants.STATE_TASK_ID_ACTIVITY, it.id)
                                        }
                                    context.startActivity(intent)
                                },
                                onLongClick = { isShowBottomSheetMenu = true }
                            )
                            .background(
                                MaterialTheme.colorScheme.onSecondaryContainer,
                                MaterialTheme.shapes.small
                            )
                            .border(
                                0.4.dp,
                                MaterialTheme.colorScheme.onSecondary,
                                MaterialTheme.shapes.small
                            ),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (isShowBottomSheetMenu) {
                            CustomDropdownMenu(
                                subTaskViewModel = subTaskViewModel,
                                task = it,
                                taskViewModel = taskViewModel,
                                onDismiss = { isShowBottomSheetMenu = it }
                            )
                        }

                        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                            val (titleRef, priorityRef) = createRefs()

                            Text(
                                text = "Title : " + it.title,
                                modifier = Modifier.constrainAs(titleRef) {
                                    start.linkTo(parent.start, 6.dp)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(priorityRef.bottom)
                                },
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Box(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .background(
                                        colorResource(priorityColor(it.priorityFlag)),
                                        MaterialTheme.shapes.small
                                    )
                                    .constrainAs(priorityRef) {
                                        end.linkTo(parent.end)
                                        top.linkTo(parent.top)
                                    }
                            ) {
                                Text(
                                    text = priorityText(it.priorityFlag),
                                    modifier = Modifier
                                        .width(60.dp)
                                        .padding(
                                            bottom = 6.dp,
                                            top = 6.dp
                                        ),
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp, end = 6.dp),
                            thickness = 0.4.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )

                        Text(
                            text = "Description : " + it.description,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp, end = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            minLines = 3,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp, end = 6.dp),
                            thickness = 0.4.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )

                        Text(
                            text = "Category : " + it.category,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp, end = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            minLines = 1,
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp, end = 6.dp),
                            thickness = 0.4.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )

                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                        ) {
                            val (stateRef, timeRef) = createRefs()
                            Row(
                                modifier = Modifier.constrainAs(stateRef) {
                                    start.linkTo(parent.start)
                                    bottom.linkTo(parent.bottom)
                                    top.linkTo(parent.top)
                                },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
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

                                it.imagePath?.let { path ->
                                    if (path.isNotEmpty()) {
                                        Icon(
                                            painter = painterResource(R.drawable.icons_image),
                                            contentDescription = it.title,
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            }

                            Text(
                                text = it.date + " - " + it.time,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.constrainAs(timeRef) {
                                    end.linkTo(parent.end)
                                    top.linkTo(stateRef.top)
                                    bottom.linkTo(stateRef.bottom)
                                },
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun priorityColor(flag: Int): Int {
    return when (flag) {
        0 -> R.color.priority_low
        1 -> R.color.priority_medium
        2 -> R.color.priority_high
        else -> {
            R.color.priority_low
        }
    }
}

@Composable
private fun priorityText(flag: Int): String {
    return when (flag) {
        0 -> "LOW"
        1 -> "MEDIUM"
        2 -> "HIGH"
        else -> {
            "LOW"
        }
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
