package com.rarestardev.taskora.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.rarestardev.taskora.R
import com.rarestardev.taskora.components.BannerAds
import com.rarestardev.taskora.components.CircleCheckBox
import com.rarestardev.taskora.components.TaskPreviewScreen
import com.rarestardev.taskora.database.NoteDatabase
import com.rarestardev.taskora.enums.ReminderType
import com.rarestardev.taskora.factory.SubTaskViewModelFactory
import com.rarestardev.taskora.factory.TaskViewModelFactory
import com.rarestardev.taskora.feature.CategorySelector
import com.rarestardev.taskora.feature.ResizableImageItem
import com.rarestardev.taskora.model.SubTask
import com.rarestardev.taskora.ui.theme.NoteMasterTheme
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.utilities.CurrentTimeAndDate
import com.rarestardev.taskora.utilities.ReminderController
import com.rarestardev.taskora.utilities.previewFakeTaskViewModel
import com.rarestardev.taskora.utilities.previewSubTaskViewModel
import com.rarestardev.taskora.view_model.SubTaskViewModel
import com.rarestardev.taskora.view_model.TaskViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

class CreateTaskActivity : BaseActivity() {

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(NoteDatabase.getInstance(this).taskItemDao())
    }

    private val subTaskViewModel: SubTaskViewModel by viewModels {
        SubTaskViewModelFactory(NoteDatabase.getInstance(this).subTaskDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.updateIsPreviewTask(intent.getBooleanExtra(Constants.STATE_TASK_ACTIVITY, false))
        val taskId = intent.getIntExtra(Constants.STATE_TASK_ID_ACTIVITY, 0)

        setComposeContent {
            if (!viewModel.isPreviewTask) {
                TaskEditorScreen(viewModel, subTaskViewModel)
            } else {
                TaskPreviewScreen(viewModel, subTaskViewModel, taskId)
                Log.d(Constants.APP_LOG, "TaskPreviewScreen item id : $taskId")
            }
        }
    }

    override fun onStop() {
        lifecycleScope.launch {
            ReminderController.clearDataStore(this@CreateTaskActivity)
        }

        Log.d(Constants.APP_LOG,"onStop")
        super.onStop()
    }

    override fun onDestroy() {
        lifecycleScope.launch {
            ReminderController.clearDataStore(this@CreateTaskActivity)
        }
        Log.d(Constants.APP_LOG,"onDestroy")
        super.onDestroy()
    }
}

@Preview
@Composable
private fun TaskActivityPreview() {
    NoteMasterTheme {
        TaskEditorScreen(
            viewModel = previewFakeTaskViewModel(),
            subTaskViewModel = previewSubTaskViewModel()
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun TaskEditorScreen(viewModel: TaskViewModel, subTaskViewModel: SubTaskViewModel) {
    val subTaskViews = getSubTaskItems(subTaskViewModel)
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBarView(viewModel, subTaskViewModel) },
        bottomBar = { BottomAppBarView(viewModel, subTaskViewModel) }
    ) { paddingValues ->

        BackHandler {
            viewModel.updateTaskId(0)
            scope.launch { ReminderController.clearDataStore(activity.applicationContext) }
            activity.finish()
        }

        Column (
            modifier = Modifier.fillMaxWidth()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = 12.dp,
                    end = 12.dp,
                    bottom = paddingValues.calculateBottomPadding()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            BannerAds()

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = rememberLazyListState()
            ) {
                item {
                    subTaskViewModel.updateSubTaskPosition(0)

                    TitleLabelTextField(viewModel = viewModel)

                    CategorySelector(viewModel = viewModel)

                    Spacer(Modifier.height(12.dp))

                    DescriptionTextField(viewModel = viewModel)

                    ReminderLayout(viewModel)

                    if (viewModel.imagePath.isNotEmpty()) {
                        ResizableImageItem(viewModel.imagePath.toUri()) {
                            viewModel.updateImagePath("")
                        }
                    }
                }

                items(subTaskViews.size) { index ->
                    subTaskViews[index]()
                }
            }
        }
    }
}

@Composable
private fun ReminderLayout(taskViewModel: TaskViewModel) {
    val currentTimeAndDate = CurrentTimeAndDate()
    val context = LocalContext.current
    val time by ReminderController.getTime(context).collectAsState(0)
    val type by ReminderController.getType(context).collectAsState("")
    val scope = rememberCoroutineScope()

    if (type != ReminderType.NONE.name && time != 0L) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(50.dp)
                .background(
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    MaterialTheme.shapes.small
                )
        ) {
            val (alarmTypeRef, alarmTimeRef, deleteButtonRef) = createRefs()

            if (type == ReminderType.NOTIFICATION.name) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.notification),
                    modifier = Modifier.constrainAs(alarmTypeRef) {
                        start.linkTo(parent.start, 12.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            } else if (type == ReminderType.ALARM.name) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_alarm),
                    contentDescription = stringResource(R.string.notification),
                    modifier = Modifier.constrainAs(alarmTypeRef) {
                        start.linkTo(parent.start, 12.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }

            Text(
                text = currentTimeAndDate.alarmTimeToText(time),
                modifier = Modifier.constrainAs(alarmTimeRef) {
                    start.linkTo(alarmTypeRef.end, 8.dp)
                    top.linkTo(alarmTypeRef.top)
                    bottom.linkTo(alarmTypeRef.bottom)
                    end.linkTo(deleteButtonRef.start, 8.dp)
                    width = Dimension.fillToConstraints
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )

            TextButton(
                onClick = {
                    taskViewModel.updateReminderTime(0L)
                    taskViewModel.updateReminderType(ReminderType.NONE)
                    scope.launch { ReminderController.clearDataStore(context) }
                },
                modifier = Modifier.constrainAs(deleteButtonRef) {
                    end.linkTo(parent.end, 12.dp)
                    top.linkTo(alarmTypeRef.top)
                    bottom.linkTo(alarmTypeRef.bottom)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text(
                    text = stringResource(R.string.delete),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

private fun getSubTaskItems(subTaskViewModel: SubTaskViewModel): List<@Composable () -> Unit> {
    val transparentColor = Color.Transparent
    return subTaskViewModel.subTaskItems.mapIndexed { index, subTask ->
        {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                val (checkBoxRef, textFieldRef, deleteRef) = createRefs()
                subTask.subChecked?.let {
                    CircleCheckBox(
                        checked = it,
                        onCheckedChange = {},
                        modifier = Modifier
                            .constrainAs(checkBoxRef) {
                                start.linkTo(parent.start,8.dp)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                    )
                }

                TextField(
                    value = subTask.subTaskDescription,
                    onValueChange = {
                        subTaskViewModel.updateDescriptionState(it)

                        if (subTask.subTaskDescription != it) {
                            subTaskViewModel.subTaskItems[index] =
                                subTask.copy(subTaskDescription = it)
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .constrainAs(textFieldRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(checkBoxRef.end, 8.dp)
                            end.linkTo(deleteRef.start, 8.dp)
                            width = Dimension.fillToConstraints
                        },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedIndicatorColor = transparentColor,
                        focusedIndicatorColor = transparentColor,
                        focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        cursorColor = MaterialTheme.colorScheme.onSecondary
                    )
                )

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
                            end.linkTo(parent.end, 26.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )
            }
        }
    }
}

@Composable
private fun DescriptionTextField(viewModel: TaskViewModel) {
    val transparentColor = Color.Transparent
    TextField(
        value = viewModel.descriptionState,
        onValueChange = { viewModel.updateDescriptionFieldValue(it) },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = stringResource(R.string.description),
                color = colorResource(R.color.text_field_label_color),
                style = MaterialTheme.typography.labelMedium
            )
        },
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedIndicatorColor = transparentColor,
            focusedIndicatorColor = transparentColor,
            focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            cursorColor = MaterialTheme.colorScheme.onSecondary,
            focusedLeadingIconColor = transparentColor,
            unfocusedLeadingIconColor = transparentColor
        ),
        shape = MaterialTheme.shapes.medium,
        minLines = 8
    )
}

@Composable
private fun TitleLabelTextField(viewModel: TaskViewModel) {
    val transparentColor = Color.Transparent
    TextField(
        value = viewModel.titleState,
        onValueChange = { viewModel.updateTitleFieldValue(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = MaterialTheme.shapes.medium,
        label = {
            Text(
                text = stringResource(R.string.note_title),
                style = MaterialTheme.typography.labelMedium,
                color = colorResource(R.color.text_field_label_color)
            )
        },
        trailingIcon = { TitleTrailingIcon(viewModel) },
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedIndicatorColor = transparentColor,
            focusedIndicatorColor = transparentColor,
            focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            cursorColor = MaterialTheme.colorScheme.onSecondary,
            focusedLeadingIconColor = transparentColor,
            unfocusedLeadingIconColor = transparentColor
        ),
        maxLines = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TitleTrailingIcon(viewModel: TaskViewModel) {
    var flagColor by remember { mutableIntStateOf(R.color.priority_low) }
    var showDropDownMenu by remember { mutableStateOf(false) }
    val flagListColor = listOf(
        "Priority Low" to R.color.priority_low,
        "Priority Medium" to R.color.priority_medium,
        "Priority High" to R.color.priority_high
    )

    ExposedDropdownMenuBox(
        expanded = showDropDownMenu,
        onExpandedChange = { showDropDownMenu = !showDropDownMenu }
    ) {
        IconButton(
            onClick = {
                showDropDownMenu = true
            },
            Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        ) {
            when (viewModel.priorityFlag) {
                0 -> flagColor = R.color.priority_low
                1 -> flagColor = R.color.priority_medium
                2 -> flagColor = R.color.priority_high
            }

            Icon(
                painter = painterResource(R.drawable.icons_flag),
                contentDescription = stringResource(R.string.setflagonnote),
                tint = colorResource(flagColor)
            )
        }

        DropdownMenu(
            expanded = showDropDownMenu,
            onDismissRequest = { showDropDownMenu = !showDropDownMenu },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(6.dp)
                .background(Color.Transparent)
        ) {
            flagListColor.forEachIndexed { index, flag ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = flag.first,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.icons_flag),
                            contentDescription = stringResource(R.string.setflagonnote),
                            tint = colorResource(flag.second)
                        )
                    },
                    onClick = {
                        viewModel.updatePriority(index)
                        showDropDownMenu = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomAppBarView(
    viewModel: TaskViewModel,
    subTaskViewModel: SubTaskViewModel
) {
    var showSubTaskSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (viewModel.taskId == 0) {
            viewModel.updateTaskId(generateUniqueTaskId(viewModel))
        }
    }

    if (showSubTaskSheet) {
        AddSubTaskBottomSheet(
            viewModel,
            subTaskViewModel
        ) {
            showSubTaskSheet = it
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.updateImagePath(uri.toString())
        }
    }

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .imePadding()
            .clip(MaterialTheme.shapes.medium)
            .padding(
                start = 10.dp,
                end = 10.dp,
                bottom = 12.dp
            ),
        containerColor = Color.Transparent
    ) {
        Row(
            horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    MaterialTheme.shapes.medium
                )
                .clip(MaterialTheme.shapes.medium)
        ) {
            BottomBarItem(R.drawable.icons_checked_checkbox) { showSubTaskSheet = true }

            BottomBarItem(R.drawable.icon_image) {
                launcher.launch("image/*")
            }

            BottomBarItem(R.drawable.icons_alarm_clock) {
                context.startActivity(Intent(context, ReminderActivity::class.java))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSubTaskBottomSheet(
    taskViewModel: TaskViewModel,
    subTaskViewModel: SubTaskViewModel,
    onDismiss: (Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val transparentColor = Color.Transparent

    ModalBottomSheet(
        onDismissRequest = { onDismiss(false) },
        sheetState = sheetState,
        scrimColor = Color.Transparent,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = subTaskViewModel.descriptionState,
                onValueChange = { subTaskViewModel.updateDescriptionState(it) },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = stringResource(R.string.description),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorResource(R.color.text_field_label_color)
                    )
                },
                minLines = 1,
                colors = TextFieldDefaults.colors().copy(
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedIndicatorColor = transparentColor,
                    focusedIndicatorColor = transparentColor,
                    focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    cursorColor = MaterialTheme.colorScheme.onSecondary,
                    focusedLeadingIconColor = transparentColor,
                    unfocusedLeadingIconColor = transparentColor
                )
            )

            TextButton(
                onClick = {
                    subTaskViewModel.updateSubTaskPosition(subTaskViewModel.subTaskPosition++)

                    subTaskViewModel.subTaskItems.add(
                        SubTask(
                            subChecked = false,
                            subTaskDescription = subTaskViewModel.descriptionState,
                            taskId = taskViewModel.taskId,
                            position = subTaskViewModel.subTaskPosition
                        )
                    )
                    subTaskViewModel.updateDescriptionState("")
                    onDismiss(false)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = MaterialTheme.colorScheme.onSecondary)
            ) {
                Text(
                    text = stringResource(R.string.add_subtask),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

private suspend fun generateUniqueTaskId(taskViewModel: TaskViewModel): Int {
    var newId: Int
    do {
        newId = Random.nextInt()
    } while (taskViewModel.checkIdInDatabase(newId))
    return newId
}

@Composable
private fun BottomBarItem(
    icon: Int,
    onClickButtons: () -> Unit
) {
    IconButton(onClick = onClickButtons) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarView(viewModel: TaskViewModel, subTaskViewModel: SubTaskViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val time by ReminderController.getTime(context).collectAsState(0)
    val type by ReminderController.getType(context).collectAsState("")

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch { ReminderController.clearDataStore(context) }
                    activity?.finish()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_activity_desc),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        actions = {
            TextButton(
                onClick = {

                    val type = when (type) {
                        ReminderType.NOTIFICATION.name -> {
                            ReminderType.NOTIFICATION
                        }

                        ReminderType.ALARM.name -> {
                            ReminderType.ALARM
                        }

                        else -> {
                            ReminderType.NONE
                        }
                    }

                    viewModel.updateReminderTime(time)
                    viewModel.updateReminderType(type)

                    viewModel.insertTask(context)
                    if (viewModel.titleState.isNotEmpty() && viewModel.descriptionState.isNotEmpty()){
                        subTaskViewModel.insertSubTask()
                    }

                    scope.launch { ReminderController.clearDataStore(context) }
                    activity?.finish()
                },
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = stringResource(R.string.save),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    )
}