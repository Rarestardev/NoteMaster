package com.rarestardev.taskora.list_item

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import com.rarestardev.taskora.R
import com.rarestardev.taskora.activities.CreateTaskActivity
import com.rarestardev.taskora.components.AdiveryNativeAdLayoutWithTitle
import com.rarestardev.taskora.enums.ReminderType
import com.rarestardev.taskora.feature.CustomText
import com.rarestardev.taskora.model.Flags
import com.rarestardev.taskora.model.ImageResource
import com.rarestardev.taskora.model.Task
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.utilities.LanguageHelper
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
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskLazyItems(
    index: Int,
    task: Task,
    subTaskViewModel: SubTaskViewModel,
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expandedIndex by remember { mutableIntStateOf(-1) }
    val subtaskList by subTaskViewModel.subTaskList.collectAsState(emptyList())
    val subtaskFilterList = subtaskList.filter { it.taskId == task.id }
    val subtaskFilterListIsComplete = subtaskFilterList.filter { it.subChecked == true }.size

    val progress =
        if (subtaskFilterList.isNotEmpty()) subtaskFilterListIsComplete.toFloat() / subtaskFilterList.size else 0.00f
    val percentage = (progress * 100).toInt()

    Box(
        modifier = modifier
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
            modifier = Modifier.fillMaxSize()
        ) {
            val (dateLayoutRef, verticalDividerRef, titleRef, descRef, stateLayoutRef, progressRef) = createRefs()

            ConstraintLayout (
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp)
                    .padding(6.dp)
                    .constrainAs(dateLayoutRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    }
            ) {

                val (createOnRef,timeRef,dateRef,priorityRef) = createRefs()

                Text(
                    text = stringResource(R.string.create_on),
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(createOnRef){
                            top.linkTo(parent.top,6.dp)
                            start.linkTo(parent.start,6.dp)
                            end.linkTo(parent.end,6.dp)
                        },
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = colorResource(R.color.drawer_text_icon_color),
                    textAlign = TextAlign.Center
                )

                CustomText(
                    text = task.time,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colorResource(R.color.drawer_text_icon_color),
                    modifier = Modifier.constrainAs(timeRef){
                        top.linkTo(createOnRef.bottom,6.dp)
                        start.linkTo(createOnRef.start)
                        end.linkTo(createOnRef.end)
                    }
                )

                CustomText(
                    text = task.date,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = colorResource(R.color.drawer_text_icon_color),
                    modifier = Modifier.constrainAs(dateRef){
                        top.linkTo(timeRef.bottom,6.dp)
                        start.linkTo(createOnRef.start)
                        end.linkTo(createOnRef.end)
                    }
                )

                FlagView(
                    modifier = Modifier.constrainAs(priorityRef){
                        start.linkTo(createOnRef.start)
                        end.linkTo(createOnRef.end)
                        bottom.linkTo(parent.bottom,6.dp)
                    },
                    color = task.priorityFlag
                )
            }

            VerticalDivider(
                modifier = Modifier.constrainAs(verticalDividerRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                    start.linkTo(dateLayoutRef.end)
                },
                thickness = 0.3.dp,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Text(
                text = task.title,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(titleRef) {
                        start.linkTo(verticalDividerRef.end, 8.dp)
                        top.linkTo(parent.top, 8.dp)
                        end.linkTo(parent.end, 8.dp)
                        width = Dimension.fillToConstraints
                    },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (LanguageHelper.isPersian(task.title)) {
                    TextAlign.Right
                } else {
                    TextAlign.Left
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = task.description,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(descRef) {
                        start.linkTo(titleRef.start)
                        end.linkTo(titleRef.end)
                        top.linkTo(titleRef.bottom, 8.dp)
                        bottom.linkTo(stateLayoutRef.top, 8.dp)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    },
                minLines = 5,
                maxLines = 5,
                textAlign = if (LanguageHelper.isPersian(task.description)) {
                    TextAlign.Right
                } else {
                    TextAlign.Left
                },
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(stateLayoutRef) {
                        start.linkTo(titleRef.start)
                        end.linkTo(titleRef.end)
                        if (subtaskFilterList.isNotEmpty()) {
                            bottom.linkTo(progressRef.top, 8.dp)
                        } else {
                            bottom.linkTo(parent.bottom, 8.dp)
                        }
                        width = Dimension.fillToConstraints
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (task.reminderType != ReminderType.NONE.name && task.reminderTime != 0L) {
                    if (task.reminderType == ReminderType.NOTIFICATION.name) {
                        NotificationView()
                    } else {
                        AlarmView()
                    }
                }

                task.imagePath?.let {
                    if (it.isNotEmpty()) {
                        Icon(
                            painter = painterResource(R.drawable.icon_image),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (subtaskFilterList.isNotEmpty()) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(progressRef) {
                            start.linkTo(titleRef.start)
                            end.linkTo(titleRef.end)
                            bottom.linkTo(parent.bottom, 8.dp)
                            width = Dimension.fillToConstraints
                        }
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
                                top.linkTo(percentageRef.top)
                                bottom.linkTo(percentageRef.bottom)
                                end.linkTo(percentageRef.start, 6.dp)
                                width = Dimension.fillToConstraints
                            },
                        color = MaterialTheme.colorScheme.onSecondary,
                        trackColor = MaterialTheme.colorScheme.background,
                        gapSize = 0.dp
                    )

                    CustomText(
                        text = "$percentage %",
                        modifier = Modifier.constrainAs(percentageRef) {
                            end.linkTo(parent.end, 4.dp)
                            top.linkTo(parent.top, 4.dp)
                            bottom.linkTo(parent.bottom)
                        },
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp
                    )
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

    Spacer(Modifier.height(6.dp))
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

    val context = LocalContext.current

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
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_SUBJECT, task.title)
                    putExtra(Intent.EXTRA_TEXT, task.description)
                    type = "text/plain"
                }

                val chooser = Intent.createChooser(shareIntent, task.title)
                context.startActivity(chooser)
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

                flagItems.forEachIndexed { index,flag ->
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
                                taskViewModel.updatePriorityFlag(index, task.id)
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

        Spacer(Modifier.height(12.dp))

        AdiveryNativeAdLayoutWithTitle(false)
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
        tint = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
private fun FlagView(modifier: Modifier = Modifier, color: Int) {
    val lang = Locale.getDefault().language
    var priority by remember { mutableStateOf("") }

    val tint = when (color) {
        0 -> colorResource(R.color.priority_low)
        1 -> colorResource(R.color.priority_medium)
        2 -> colorResource(R.color.priority_high)
        else -> {
            colorResource(R.color.priority_low)
        }
    }

    if (lang == "en") {
        priority = when (color) {
            0 -> LanguageHelper.getEnLanguageListPriorityFlag(R.string.low)
            1 -> LanguageHelper.getEnLanguageListPriorityFlag(R.string.medium)
            2 -> LanguageHelper.getEnLanguageListPriorityFlag(R.string.high)
            else -> {
                LanguageHelper.getEnLanguageListPriorityFlag(R.string.low)
            }
        }
    } else if (lang == "fa") {
        priority = when (color) {
            0 -> LanguageHelper.getFaLanguageListPriorityFlag(R.string.low)
            1 -> LanguageHelper.getFaLanguageListPriorityFlag(R.string.medium)
            2 -> LanguageHelper.getFaLanguageListPriorityFlag(R.string.high)
            else -> {
                LanguageHelper.getFaLanguageListPriorityFlag(R.string.low)
            }
        }
    }

    Column(
        modifier = modifier
            .width(55.dp)
            .height(30.dp)
            .background(tint, MaterialTheme.shapes.small),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = priority,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AlarmView(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.icon_alarm),
        contentDescription = stringResource(R.string.alarm_desc),
        modifier = modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.onPrimary
    )
}