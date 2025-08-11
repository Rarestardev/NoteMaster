package com.rarestardev.taskora.components

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rarestardev.taskora.R
import com.rarestardev.taskora.enums.ReminderType
import com.rarestardev.taskora.feature.CustomImageView
import com.rarestardev.taskora.model.Task
import com.rarestardev.taskora.ui.theme.TaskoraTheme
import com.rarestardev.taskora.utilities.CurrentTimeAndDate
import com.rarestardev.taskora.utilities.LanguageHelper
import com.rarestardev.taskora.utilities.previewFakeTaskViewModel
import com.rarestardev.taskora.utilities.previewSubTaskViewModel
import com.rarestardev.taskora.view_model.SubTaskViewModel
import com.rarestardev.taskora.view_model.TaskViewModel
import java.util.Locale

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */

@Preview
@Composable
private fun TaskActivityPreview() {
    TaskoraTheme {
        TaskPreviewScreen(
            previewFakeTaskViewModel(),
            previewSubTaskViewModel(),
            1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskPreviewScreen(
    viewModel: TaskViewModel,
    subTaskViewModel: SubTaskViewModel,
    taskId: Int
) {
    val allTask by viewModel.taskElement.collectAsState(emptyList())
    val filterTaskWithId = allTask.filter { it.id == taskId }
    var titleActivity by remember { mutableStateOf("Preview") }
    var isDoneTask by remember { mutableStateOf(false) }
    val allSubTask by subTaskViewModel.subTaskList.collectAsState(emptyList())

    val context = LocalContext.current
    val activity = context as Activity
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.task_tick_sound)
    }

    var taskInstance by remember { mutableStateOf(Task()) }
    var textColor = MaterialTheme.colorScheme.onPrimary
    var textDecoration by remember { mutableStateOf(TextDecoration.None) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (!isDoneTask) {
                        viewModel.updateIsTaskComplete(taskId, true)
                        viewModel.updateReminder(ReminderType.NONE.name, taskId)

                        viewModel.scheduleReminder(
                            context,
                            0L,
                            "",
                            "",
                            taskId,
                            ReminderType.NONE
                        )

                        if (mediaPlayer != null && mediaPlayer.isPlaying) {
                            mediaPlayer.seekTo(0)
                        }

                        mediaPlayer.start()

                        Toast.makeText(
                            context,
                            context.getString(R.string.good_job),
                            Toast.LENGTH_SHORT
                        ).show()
                        activity.finish()
                    } else
                        viewModel.updateIsTaskComplete(taskId, false)
                },
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isDoneTask) {
                        FabContent(
                            fabIcon = Icons.Default.Refresh,
                            fabText = stringResource(R.string.rebuild)
                        )
                    } else {
                        FabContent(
                            fabIcon = Icons.Default.Done,
                            fabText = stringResource(R.string.done)
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        topBar = {
            TopAppBar(
                navigationIcon = { BackActivityIcon() },
                title = { TitleActivityText(titleActivity) },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.background,
                ),

                actions = {
                    if (!isDoneTask) {
                        TextButton(
                            onClick = {
                                viewModel.updateIsPreviewTask(false)
                                filterTaskWithId.forEach { task ->
                                    viewModel.updateAllValueForEditing(task)
                                    subTaskViewModel.subTaskItems.clear()
                                    val filterSubTaskWithTitle =
                                        allSubTask.filter { it.taskId == task.id }
                                    filterSubTaskWithTitle.forEach {
                                        subTaskViewModel.subTaskItems.add(it)
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.edit_note),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValue ->

        filterTaskWithId.forEach { task ->
            taskInstance = task
            task.isComplete?.let { isDoneTask = it }
            titleActivity = task.title
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingValue.calculateTopPadding() + 12.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = rememberLazyListState()
        ) {
            item {
                BannerAds()

                Spacer(Modifier.height(12.dp))

                PriorityLayout(taskInstance)

                Spacer(Modifier.height(12.dp))

                CategoryView(taskInstance)

                Spacer(Modifier.height(12.dp))

                ReminderInfoView(taskInstance)

                DescriptionView(taskInstance.description)

                Spacer(Modifier.height(12.dp))

                taskInstance.imagePath?.let { uri ->
                    if (uri.isNotEmpty()) {
                        CustomImageView(
                            uri,
                            showDelete = false
                        ) {}
                    }
                }
            }

            val filterSubTaskWithTitle = allSubTask.filter { it.taskId == taskInstance.id }

            item {
                Spacer(Modifier.height(12.dp))

                if (filterSubTaskWithTitle.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.subTask),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(12.dp))
            }

            items(filterSubTaskWithTitle) { subTask ->
                ConstraintLayout(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.onSecondaryContainer,
                            MaterialTheme.shapes.small
                        )
                        .padding(6.dp)
                ) {
                    val (checkBoxRef, descRef) = createRefs()
                    subTask.subChecked?.let {
                        if (it) {
                            textDecoration = TextDecoration.LineThrough
                            textColor = colorResource(R.color.text_field_label_color)
                        } else {
                            textDecoration = TextDecoration.None
                            textColor = MaterialTheme.colorScheme.onPrimary
                        }
                        CircleCheckBox(
                            checked = it,
                            onCheckedChange = { b ->
                                subTaskViewModel.updateSubTaskIsComplete(
                                    b,
                                    subTask.subTaskId
                                )
                            },
                            modifier = Modifier.constrainAs(checkBoxRef) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                        )
                    }

                    Text(
                        text = subTask.subTaskDescription,
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor,
                        textDecoration = textDecoration,
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(descRef) {
                                start.linkTo(checkBoxRef.end, 6.dp)
                                end.linkTo(parent.end, 6.dp)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                            }
                    )
                }

                Spacer(Modifier.height(3.dp))
            }

            item {
                Spacer(Modifier.height(120.dp))
            }
        }
    }
}

@Composable
private fun DescriptionView(description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.shapes.small)
            .padding(12.dp)
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            minLines = 6
        )
    }
}

@Composable
private fun ReminderInfoView(task: Task) {
    val currentTimeAndDate = CurrentTimeAndDate()
    if (task.reminderType != ReminderType.NONE.name) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    MaterialTheme.shapes.small
                )
                .padding(
                    12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.reminder) + " : " + task.reminderType,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = currentTimeAndDate.alarmTimeToText(task.reminderTime),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CategoryView(task: Task) {
    var category by remember { mutableStateOf("") }

    val lang = Locale.getDefault().language
    if (lang == "fa") {
        LanguageHelper.getEnLanguageListCategory().forEachIndexed { index, string ->
            if (task.category == string) {
                category = LanguageHelper.getFaLanguageListCategory()[index]
            }
        }
    } else {
        category = task.category!!
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.shapes.small)
            .padding(
                12.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.category),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = category,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PriorityLayout(task: Task) {
    var priority by remember { mutableStateOf("") }
    val lang = Locale.getDefault().language

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.shapes.small)
            .padding(
                12.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (lang == "en") {
            priority = when (task.priorityFlag) {
                0 -> LanguageHelper.getEnLanguageListPriorityFlag(R.string.low)
                1 -> LanguageHelper.getEnLanguageListPriorityFlag(R.string.medium)
                2 -> LanguageHelper.getEnLanguageListPriorityFlag(R.string.high)
                else -> {
                    LanguageHelper.getEnLanguageListPriorityFlag(R.string.low)
                }
            }
        } else if (lang == "fa") {
            priority = when (task.priorityFlag) {
                0 -> LanguageHelper.getFaLanguageListPriorityFlag(R.string.low)
                1 -> LanguageHelper.getFaLanguageListPriorityFlag(R.string.medium)
                2 -> LanguageHelper.getFaLanguageListPriorityFlag(R.string.high)
                else -> {
                    LanguageHelper.getFaLanguageListPriorityFlag(R.string.low)
                }
            }
        }

        val priorityColor = when (task.priorityFlag) {
            0 -> colorResource(R.color.priority_low)
            1 -> colorResource(R.color.priority_medium)
            2 -> colorResource(R.color.priority_high)
            else -> {
                colorResource(R.color.priority_low)
            }
        }

        Text(
            text = stringResource(R.string.priority),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = priority,
            style = MaterialTheme.typography.labelLarge,
            color = priorityColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TitleActivityText(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onPrimary,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun FabContent(fabText: String, fabIcon: ImageVector) {
    Icon(
        imageVector = fabIcon,
        contentDescription = fabText,
        tint = Color.White
    )

    Text(
        text = fabText,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
}

@Composable
private fun BackActivityIcon() {
    val context = LocalContext.current
    val activity = context as? Activity
    IconButton(
        onClick = { activity?.finish() }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back_activity_desc),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}