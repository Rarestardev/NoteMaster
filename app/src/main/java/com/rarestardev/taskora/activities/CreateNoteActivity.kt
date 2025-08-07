package com.rarestardev.taskora.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonColors
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rarestardev.taskora.R
import com.rarestardev.taskora.components.BannerAds
import com.rarestardev.taskora.database.NoteDatabase
import com.rarestardev.taskora.factory.NoteViewModelFactory
import com.rarestardev.taskora.feature.CustomText
import com.rarestardev.taskora.model.Note
import com.rarestardev.taskora.ui.theme.NoteMasterTheme
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.utilities.CurrentTimeAndDate
import com.rarestardev.taskora.utilities.previewFakeViewModel
import com.rarestardev.taskora.view_model.NoteEditorViewModel

class CreateNoteActivity : BaseActivity() {

    private val viewModel: NoteEditorViewModel by viewModels {
        NoteViewModelFactory(NoteDatabase.getInstance(this).noteDao())
    }

    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.updateIsEditing(intent.getBooleanExtra(Constants.STATE_NOTE_ACTIVITY, false))
        if (viewModel.isEditing) {
            note = Note(
                id = intent.getIntExtra("noteId", 0),
                noteText = intent.getStringExtra("noteText")!!,
                noteTitle = intent.getStringExtra("noteTitle")!!,
                priority = intent.getIntExtra("priority", 0),
                timeStamp = intent.getStringExtra("timeStamp")!!,
                date = intent.getStringExtra("date")!!,
                fontWeight = intent.getIntExtra("fontWeight", 0),
                fontSize = intent.getFloatExtra("fontSize", 14f)
            )
        }
        Log.d(Constants.APP_LOG, "${viewModel.isEditing}")

        setComposeContent {
            if (!viewModel.isEditing) {
                CreateNote(viewModel)
            } else {
                NotePreviewScreen(
                    note,
                    viewModel
                )
            }
        }
    }
}

@Preview
@Composable
private fun CreateNoteActivityPreview() {
    NoteMasterTheme {
        CreateNote(previewFakeViewModel())
        NotePreviewScreen(
            Note(0, "Text", "Note", 1, "2022", "20:22", 400, 14f),
            previewFakeViewModel()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun NotePreviewScreen(note: Note, viewModel: NoteEditorViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    val transparentColor = Color.Transparent
    var priorityColor by remember { mutableIntStateOf(R.color.priority_low) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = note.noteTitle,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            activity?.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.back_activity_desc),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.updateIsEditing(false)
                            viewModel.updateAllValue(
                                noteId = note.id,
                                titleTextFieldState = note.noteTitle,
                                noteTextFieldState = note.noteText,
                                priorityFlag = note.priority,
                                fontWeight = note.fontWeight,
                                fontSize = note.fontSize
                            )
                        },
                        colors = ButtonColors(
                            transparentColor,
                            transparentColor,
                            transparentColor,
                            transparentColor
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.edit_note),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    when (note.priority) {
                        0 -> priorityColor = R.color.priority_low
                        1 -> priorityColor = R.color.priority_medium
                        2 -> priorityColor = R.color.priority_high
                    }

                    Icon(
                        painter = painterResource(R.drawable.icons_flag),
                        contentDescription = note.noteTitle,
                        tint = colorResource(priorityColor),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.wrapContentHeight(),
                containerColor = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(6.dp)
                        .imePadding()
                        .background(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomText(
                        text = note.timeStamp,
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )

                    CustomText(
                        text = note.date,
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding() + 8.dp,
                    bottom = paddingValues.calculateBottomPadding() + 12.dp,
                    start = 12.dp,
                    end = 12.dp
                )
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(8.dp)
                )
                .fillMaxSize()
                .clickable {
                    viewModel.updateIsEditing(false)
                    viewModel.updateAllValue(
                        noteId = note.id,
                        titleTextFieldState = note.noteTitle,
                        noteTextFieldState = note.noteText,
                        priorityFlag = note.priority,
                        fontWeight = note.fontWeight,
                        fontSize = note.fontSize
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            BannerAds()

            Text(
                text = note.noteText,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .verticalScroll(state = rememberScrollState()),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight(note.fontWeight),
                fontSize = note.fontSize.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun CreateNote(viewModel: NoteEditorViewModel) {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CustomTopBar(viewModel) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingValues.calculateTopPadding() + 12.dp,
                    start = 8.dp,
                    end = 8.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BannerAds()

            Spacer(Modifier.height(16.dp))
            ConstraintLayout(
                Modifier.fillMaxHeight()
            ) {
                val (titleRef, noteRef, stateLayoutRef) = createRefs()

                TitleTextField(
                    viewModel = viewModel,
                    Modifier
                        .fillMaxWidth()
                        .constrainAs(titleRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        }
                )

                NoteTextField(
                    viewModel,
                    Modifier
                        .fillMaxSize()
                        .constrainAs(noteRef) {
                            top.linkTo(titleRef.bottom, 12.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(stateLayoutRef.top, 12.dp)
                            height = Dimension.fillToConstraints
                        }
                )

                StateLayout(
                    viewModel,
                    Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .background(
                            MaterialTheme.colorScheme.onSecondaryContainer,
                            RoundedCornerShape(12.dp)
                        )
                        .height(40.dp)
                        .constrainAs(stateLayoutRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom, 12.dp)
                        }
                )

                BackHandler {
                    val timeAndDate = CurrentTimeAndDate()
                    viewModel.saveNoteInDatabase(
                        context = context,
                        timeStamp = timeAndDate.currentTime(),
                        date = timeAndDate.getTodayDate()
                    )
                }
            }
        }
    }
}

@Composable
private fun StateLayout(viewModel: NoteEditorViewModel, modifier: Modifier) {
    val currentTimeAndDate = CurrentTimeAndDate()

    ConstraintLayout(
        modifier = modifier
    ) {
        CustomText(
            text = currentTimeAndDate.getTodayDate(),
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(createRef()) {
                    start.linkTo(parent.start, 8.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        CustomText(
            text = "( ${currentTimeAndDate.currentTime()} )",
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(createRef()) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        )

        CustomText(
            text = stringResource(R.string.character) + " " + viewModel.noteTextFieldState.length,
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(createRef()) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end, 8.dp)
                }
        )
    }
}

@Composable
private fun NoteTextField(viewModel: NoteEditorViewModel, modifier: Modifier) {
    val transparentColor = Color.Transparent

    TextField( // title
        value = viewModel.noteTextFieldState,
        onValueChange = { viewModel.updateNoteTextFieldState(it) },
        label = { if (viewModel.noteTextFieldState.isEmpty()) LabelTextField(stringResource(R.string.note_start_type)) },
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedIndicatorColor = transparentColor,
            focusedIndicatorColor = transparentColor,
            focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = transparentColor,
            cursorColor = MaterialTheme.colorScheme.onSecondary
        ),
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        textStyle = TextStyle.Default.copy(
            fontWeight = viewModel.fontWeight,
            fontStyle = FontStyle.Normal,
            fontSize = viewModel.fontSize.sp
        )
    )
}

@Composable
private fun TitleTextField(viewModel: NoteEditorViewModel, modifier: Modifier) {
    val transparentColor = Color.Transparent

    TextField( // title
        value = viewModel.titleTextFieldState,
        onValueChange = { viewModel.updateTitleTextFieldState(it) },
        label = { LabelTextField(stringResource(R.string.title)) },
        trailingIcon = { LeadingTextFieldIcon(viewModel) },
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedIndicatorColor = transparentColor,
            focusedIndicatorColor = transparentColor,
            focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            cursorColor = MaterialTheme.colorScheme.onSecondary,
            focusedLeadingIconColor = transparentColor,
            unfocusedLeadingIconColor = transparentColor
        ),
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        maxLines = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeadingTextFieldIcon(viewModel: NoteEditorViewModel) {
    var flagColor by remember { mutableIntStateOf(R.color.priority_low) }
    var showDropDownMenu by remember { mutableStateOf(false) }
    val flagListColor = listOf(
        stringResource(R.string.low) to R.color.priority_low,
        stringResource(R.string.medium) to R.color.priority_medium,
        stringResource(R.string.high) to R.color.priority_high
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
                        Log.d(Constants.APP_LOG, "Flag index = $index")
                    }
                )
            }
        }
    }
}

@Composable
private fun LabelTextField(value: String) {
    Text(
        text = value,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = colorResource(R.color.text_field_label_color)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTopBar(viewModel: NoteEditorViewModel) {
    val context = LocalContext.current

    TopAppBar(
        title = {
            // Not used
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    val timeAndDate = CurrentTimeAndDate()
                    viewModel.saveNoteInDatabase(
                        context = context,
                        timeStamp = timeAndDate.currentTime(),
                        date = timeAndDate.getTodayDate()
                    )
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
            containerColor = MaterialTheme.colorScheme.background
        ),
        actions = { MoreFeatureMenu(viewModel) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreFeatureMenu(viewModel: NoteEditorViewModel) {
    ExposedDropdownMenuBox(
        expanded = viewModel.showMoreFeatureMenu,
        onExpandedChange = { viewModel.updateShowMoreFeatureMenu(it) }
    ) {
        IconButton(
            onClick = {
                viewModel.updateShowMoreFeatureMenu(true)
            },
            Modifier.menuAnchor(MenuAnchorType.SecondaryEditable, true)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.more_features),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        DropdownMenu(
            expanded = viewModel.showMoreFeatureMenu,
            onDismissRequest = { viewModel.updateShowMoreFeatureMenu(false) },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(8.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.font_weight),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                onClick = {
                    viewModel.updateShowMoreFontWeight(true)
                    viewModel.updateShowMoreFeatureMenu(false)
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.font_size),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                onClick = {
                    viewModel.updateShowMoreFontSize(true)
                    viewModel.updateShowMoreFeatureMenu(false)
                }
            )
        }
    }

    FontWeightBottomSheet(viewModel)
    FontSizeBottomSheet(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontWeightBottomSheet(viewModel: NoteEditorViewModel) {
    val fontWeightList = listOf(
        "Normal" to FontWeight.Normal,
        "Bold" to FontWeight.Bold,
        "SemiBold" to FontWeight.SemiBold,
        "ExtraBold" to FontWeight.ExtraBold,
        "Black" to FontWeight.Black,
        "Medium" to FontWeight.Medium,
        "Light" to FontWeight.Light,
        "ExtraLight" to FontWeight.ExtraLight,
        "Thin" to FontWeight.Thin
    )

    val lazyState = rememberLazyListState()

    if (viewModel.showMoreFontWeight) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.updateShowMoreFontWeight(false) },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.background,
            scrimColor = Color.Transparent
        ) {
            LazyColumn(
                state = lazyState,
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "Font Weight",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }

                items(fontWeightList) { weight ->
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(
                                start = 12.dp,
                                end = 12.dp
                            )
                            .clickable {
                                viewModel.updateFontWeight(weight.second)
                                viewModel.updateShowMoreFontWeight(false)
                            }
                    ) {
                        Text(
                            text = weight.first,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.constrainAs(createRef()) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                        )

                        Text(
                            text = stringResource(R.string.sample),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = weight.second,
                            modifier = Modifier.constrainAs(createRef()) {
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontSizeBottomSheet(viewModel: NoteEditorViewModel) {
    if (viewModel.showMoreFontSize) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.updateShowMoreFontSize(false) },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.background,
            scrimColor = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.font_size_bottom_sheet),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                )

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    val (minSizeRef, maxSizeRef, sliderRef) = createRefs()

                    Text(
                        text = "14",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.constrainAs(minSizeRef) {
                            start.linkTo(parent.start, 8.dp)
                            top.linkTo(parent.top)
                        }
                    )

                    Text(
                        text = "50",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.constrainAs(maxSizeRef) {
                            end.linkTo(parent.end, 8.dp)
                            top.linkTo(parent.top)
                        }
                    )

                    Slider(
                        value = viewModel.fontSize,
                        onValueChange = { viewModel.updateFontSize(it) },
                        valueRange = 12f..50f,
                        steps = 38,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .constrainAs(sliderRef) {
                                top.linkTo(minSizeRef.bottom, 12.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        colors = SliderDefaults.colors().copy(
                            thumbColor = MaterialTheme.colorScheme.onSecondary,
                            activeTrackColor = MaterialTheme.colorScheme.onSecondary,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}