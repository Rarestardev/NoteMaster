package com.rarestardev.notemaster.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.dao.SubTaskDao
import com.rarestardev.notemaster.dao.TaskItemDao
import com.rarestardev.notemaster.database.NoteDatabase
import com.rarestardev.notemaster.factory.SubTaskViewModelFactory
import com.rarestardev.notemaster.feature.CategorySelector
import com.rarestardev.notemaster.feature.ReminderBottomSheet
import com.rarestardev.notemaster.model.Task
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.view_model.TaskViewModel
import com.rarestardev.notemaster.factory.TaskViewModelFactory
import com.rarestardev.notemaster.feature.ResizableImageItem
import com.rarestardev.notemaster.model.SubTask
import com.rarestardev.notemaster.view_model.SubTaskViewModel
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.rarestardev.notemaster.utilities.previewFakeTaskViewModel
import com.rarestardev.notemaster.utilities.previewSubTaskViewModel
import kotlinx.coroutines.launch

class CreateTaskActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(NoteDatabase.getInstance(this).taskItemDao())
    }

    private val subTaskViewModel: SubTaskViewModel by viewModels {
        SubTaskViewModelFactory(NoteDatabase.getInstance(this).subTaskDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteMasterTheme {
                TaskEditorScreen(viewModel, subTaskViewModel)
            }
        }
    }
}

@Preview
@Composable
private fun TaskActivityPreview() {
    NoteMasterTheme(darkTheme = true) {
        TaskEditorScreen(viewModel = previewFakeTaskViewModel(), subTaskViewModel = previewSubTaskViewModel())
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun TaskEditorScreen(viewModel: TaskViewModel, subTaskViewModel: SubTaskViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBarView(viewModel,subTaskViewModel) },
        bottomBar = { BottomAppBarView(viewModel, subTaskViewModel) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding() + 12.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = paddingValues.calculateBottomPadding() + 12.dp
                )
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            subTaskViewModel.updateSubTaskPosition(0)

            TitleLabelTextField(viewModel = viewModel)

            CategorySelector(viewModel = viewModel)

            DescriptionTextField(viewModel = viewModel)

            if (viewModel.imagePath.isNotEmpty()) {
                ResizableImageItem(viewModel.imagePath.toUri()) {
                    viewModel.updateImagePath("")
                }
            }

            SubTaskLazyColumn(subTaskViewModel)
        }
    }
}

@Composable
private fun SubTaskLazyColumn(subTaskViewModel: SubTaskViewModel) {
    val transparentColor = Color.Transparent
    if (subTaskViewModel.subTaskList.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = rememberLazyListState()
        ) {
            items(subTaskViewModel.subTaskList) { subTask ->
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.onSecondaryContainer,
                            MaterialTheme.shapes.medium
                        )
                ) {
                    val (checkBoxRef, textFieldRef, deleteRef) = createRefs()
                    Checkbox(
                        checked = false,
                        onCheckedChange = {},
                        modifier = Modifier.constrainAs(checkBoxRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                    )

                    TextField(
                        value = subTask.subTaskDescription,
                        onValueChange = { subTaskViewModel.updateDescriptionState(it) },
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 8.dp
                            )
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
                            cursorColor = MaterialTheme.colorScheme.onSecondary,
                            focusedLeadingIconColor = transparentColor,
                            unfocusedLeadingIconColor = transparentColor
                        )
                    )

                    Text(
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .clickable {
                                subTaskViewModel.viewModelScope.launch {
                                    subTaskViewModel.subTaskList.remove(subTask)
                                }
                            }
                            .constrainAs(deleteRef) {
                                end.linkTo(parent.end, 12.dp)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                    )
                }

                Spacer(Modifier.height(8.dp))
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
        minLines = 5
    )
}

@Composable
private fun TitleLabelTextField(viewModel: TaskViewModel) {
    val transparentColor = Color.Transparent
    TextField(
        value = viewModel.titleState,
        onValueChange = { viewModel.updateTitleFieldValue(it) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        label = {
            Text(
                text = stringResource(R.string.note_title),
                style = MaterialTheme.typography.labelMedium,
                color = colorResource(R.color.text_field_label_color)
            )
        },
        isError = viewModel.isError,
        supportingText = {
            if (viewModel.isError) {
                Text(
                    text = "Title is repeating!"
                )
            }
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
private fun BottomAppBarView(viewModel: TaskViewModel, subTaskViewModel: SubTaskViewModel) {
    var showReminderSheet by remember { mutableStateOf(false) }
    var showSubTaskSheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val transparentColor = Color.Transparent
    val context = LocalContext.current

    if (showReminderSheet) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ReminderBottomSheet(
                onDismiss = { showReminderSheet = false },
                onSet = { timeMillis, type ->
                    viewModel.updateReminderTime(timeMillis)
                    viewModel.updateReminderType(type)
                }
            )
        }
    }

    if (showSubTaskSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSubTaskSheet = false },
            sheetState = sheetState,
            scrimColor = Color.Transparent,
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
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
                        if (viewModel.titleState.isNotEmpty() && !viewModel.isError) {
                            subTaskViewModel.subTaskList.add(
                                SubTask(
                                    subChecked = false,
                                    subTaskDescription = subTaskViewModel.descriptionState,
                                    taskTitle = viewModel.titleState,
                                    position = subTaskViewModel.subTaskPosition
                                )
                            )
                            subTaskViewModel.updateDescriptionState("")
                            showSubTaskSheet = false
                        } else {
                            Toast.makeText(
                                context,
                                "Please fill the title field!",
                                Toast.LENGTH_LONG
                            ).show()

                            showSubTaskSheet = false
                        }
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

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.updateImagePath(uri.toString())
        }
    }

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
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

            BottomBarItem(R.drawable.icons_image) {
                launcher.launch("image/*")
            }

            BottomBarItem(R.drawable.icons_alarm_clock) { showReminderSheet = true }
        }
    }
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

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = { activity?.finish() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_activity_desc),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            Button(
                onClick = {
                    viewModel.insertTask(context)
                    subTaskViewModel.insertSubTask()
                    activity?.finish()
                },
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = stringResource(R.string.save),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    )
}