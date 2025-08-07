package com.rarestardev.taskora.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.rarestardev.taskora.R
import com.rarestardev.taskora.enums.ReminderType
import com.rarestardev.taskora.feature.ResizableImageItem
import com.rarestardev.taskora.model.Task
import com.rarestardev.taskora.ui.theme.NoteMasterTheme
import com.rarestardev.taskora.utilities.CurrentTimeAndDate
import com.rarestardev.taskora.utilities.previewFakeTaskViewModel
import com.rarestardev.taskora.utilities.previewSubTaskViewModel
import com.rarestardev.taskora.view_model.SubTaskViewModel
import com.rarestardev.taskora.view_model.TaskViewModel
import kotlinx.coroutines.launch
import java.util.Locale


@Preview
@Composable
private fun TaskActivityPreview() {
    NoteMasterTheme {
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

        Column(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingValue.calculateTopPadding() + 12.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            filterTaskWithId.forEach { task ->
                task.isComplete?.let { isDoneTask = it }
                titleActivity = task.title
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BannerAds()

                    Spacer(Modifier.height(16.dp))

                    PriorityLayout(task)

                    CategoryView(task)

                    ReminderInfoView(task)

                    DescriptionView(task.description)

                    task.imagePath?.let { uri ->
                        if (uri.isNotEmpty()) {
                            ResizableImageItem(uri.toUri()) {}
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    SubTaskLazy(task.id, subTaskViewModel)
                }
            }

            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
private fun SubTaskLazy(
    taskId: Int,
    subTaskViewModel: SubTaskViewModel
) {
    val allSubTask by subTaskViewModel.subTaskList.collectAsState(emptyList())
    val filterSubTaskWithTitle = allSubTask.filter { it.taskId == taskId }
    var textDecoration by remember { mutableStateOf(TextDecoration.None) }
    var textColor = MaterialTheme.colorScheme.onPrimary

    if (filterSubTaskWithTitle.isNotEmpty()) {
        Text(
            text = "SubTasks",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = rememberLazyListState()
    ) {
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
                val (deleteRef, checkBoxRef, descRef) = createRefs()
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
                    text = stringResource(R.string.delete),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .clickable {
                            subTaskViewModel.viewModelScope.launch {
                                subTaskViewModel.subTaskItems.remove(subTask)
                            }
                        }
                        .constrainAs(deleteRef) {
                            end.linkTo(parent.end, 12.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )

                Text(
                    text = subTask.subTaskDescription,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                    textDecoration = textDecoration,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(descRef) {
                            start.linkTo(checkBoxRef.end, 6.dp)
                            end.linkTo(deleteRef.start, 6.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                        }
                )
            }

            Spacer(Modifier.height(3.dp))
        }
    }
}

@Composable
private fun DescriptionView(description: String) {
    Spacer(modifier = Modifier.height(12.dp))

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
        getEnLanguageListCategory().forEachIndexed { index, string ->
            if (task.category == string) {
                category = getFaLanguageListCategory()[index]
            }
        }
    }else{
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
private fun getEnLanguageListCategory() : Array<String> {
    val context = LocalContext.current

    val config = Configuration(context.resources.configuration)
    config.setLocale(Locale("en"))
    val localizedContext = context.createConfigurationContext(config)
    return localizedContext.resources.getStringArray(R.array.task_categories)
}

@Composable
private fun getFaLanguageListCategory() : Array<String> {
    val context = LocalContext.current

    val config = Configuration(context.resources.configuration)
    config.setLocale(Locale("fa"))
    val localizedContext = context.createConfigurationContext(config)
    return localizedContext.resources.getStringArray(R.array.task_categories)
}

@Composable
private fun PriorityLayout(task: Task) {
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
        val priority = when (task.priorityFlag) {
            0 -> "LOW"
            1 -> "MEDIUM"
            2 -> "HIGH"
            else -> {
                "LOW"
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