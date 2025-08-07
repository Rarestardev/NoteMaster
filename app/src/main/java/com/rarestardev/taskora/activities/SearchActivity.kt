package com.rarestardev.taskora.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.components.BannerAds
import com.rarestardev.taskora.database.NoteDatabase
import com.rarestardev.taskora.enums.SearchFilter
import com.rarestardev.taskora.factory.NoteViewModelFactory
import com.rarestardev.taskora.factory.TaskViewModelFactory
import com.rarestardev.taskora.feature.CustomText
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.view_model.NoteEditorViewModel
import com.rarestardev.taskora.view_model.TaskViewModel

class SearchActivity : BaseActivity() {

    private val noteEditorViewModel: NoteEditorViewModel by viewModels {
        NoteViewModelFactory(NoteDatabase.getInstance(this).noteDao())
    }

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(NoteDatabase.getInstance(this).taskItemDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setComposeContent {
            SearchActivityScreen(taskViewModel, noteEditorViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun SearchActivityScreen(
    taskViewModel: TaskViewModel,
    noteEditorViewModel: NoteEditorViewModel
) {
    val activity = LocalContext.current as? Activity
    var filters by remember { mutableStateOf(SearchFilter.TASK) }
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.search),
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
                },
                actions = {
                    filters = buttonFilters()
                }
            )
        },
        bottomBar = { query = myBottomBar() }

    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValue.calculateTopPadding() + 8.dp,
                    bottom = paddingValue.calculateBottomPadding() + 8.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            BannerAds()

            if (filters == SearchFilter.TASK) {
                if (query.isNotEmpty()) taskViewModel.updateQuery(query)
                TaskItems(taskViewModel)

            } else if (filters == SearchFilter.NOTE) {
                if (query.isNotEmpty()) noteEditorViewModel.updateQuery(query)
                NoteItems(noteEditorViewModel)
            }
        }
    }
}

@Composable
private fun TaskItems(taskViewModel: TaskViewModel) {
    val result by taskViewModel.result.collectAsState()
    val lazyState = rememberLazyListState()
    val context = LocalContext.current

    if (result.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = lazyState
        ) {
            item {
                CustomText(
                    text = stringResource(R.string.result) + " : " + result.size,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(Modifier.height(12.dp))
            }

            items(result) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.onSecondaryContainer,
                            MaterialTheme.shapes.small
                        )
                        .clickable {
                            val intent = Intent(context, CreateTaskActivity::class.java).apply {
                                putExtra(Constants.STATE_TASK_ACTIVITY, true)
                                putExtra(Constants.STATE_TASK_ID_ACTIVITY, it.id)
                            }
                            context.startActivity(intent)
                        }
                        .border(
                            0.4.dp,
                            MaterialTheme.colorScheme.onSecondary,
                            MaterialTheme.shapes.small
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.title) + it.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Start
                    )

                    CustomText(
                        text = it.date + " - " + it.time,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    } else {
        Text(
            text = stringResource(R.string.no_task),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NoteItems(viewModel: NoteEditorViewModel) {
    val resultSearch by viewModel.result.collectAsState()
    val lazyState = rememberLazyListState()
    val context = LocalContext.current

    if (resultSearch.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = lazyState
        ) {
            item {
                CustomText(
                    text = stringResource(R.string.result) + " : " + resultSearch.size,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(Modifier.height(12.dp))
            }

            items(resultSearch) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.onSecondaryContainer,
                            MaterialTheme.shapes.small
                        )
                        .clickable {
                            val previewNoteIntent =
                                Intent(context, CreateNoteActivity::class.java)
                                    .putExtra("noteId", it.id)
                                    .putExtra("noteTitle", it.noteTitle)
                                    .putExtra("noteText", it.noteText)
                                    .putExtra("priority", it.priority)
                                    .putExtra("timeStamp", it.timeStamp)
                                    .putExtra("date", it.date)
                                    .putExtra("fontWeight", it.fontWeight)
                                    .putExtra("fontSize", it.fontSize)
                                    .putExtra(Constants.STATE_NOTE_ACTIVITY, true)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                            context.startActivity(previewNoteIntent)
                        }
                        .border(
                            0.4.dp,
                            MaterialTheme.colorScheme.onSecondary,
                            MaterialTheme.shapes.small
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.title) + it.noteTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Start
                    )

                    CustomText(
                        text = it.date + " - " + it.timeStamp,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    } else {
        Text(
            text = stringResource(R.string.no_note),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun buttonFilters(): SearchFilter {
    var showDropDownMenu by remember { mutableStateOf(false) }
    val listFilter = listOf(stringResource(R.string.task_bottom_bar), stringResource(R.string.note))

    var filters by remember { mutableStateOf(listFilter[0]) }
    var filterState by remember { mutableStateOf(SearchFilter.TASK) }

    ExposedDropdownMenuBox(
        expanded = showDropDownMenu,
        onExpandedChange = { showDropDownMenu = !showDropDownMenu }
    ) {
        TextButton(
            onClick = { showDropDownMenu = true },
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .padding(6.dp)
                .menuAnchor(MenuAnchorType.SecondaryEditable, true),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = stringResource(R.string.filter) + " : " + filters,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }

        DropdownMenu(
            expanded = showDropDownMenu,
            onDismissRequest = { showDropDownMenu = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = listFilter[0],
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(6.dp)
                    )
                },
                onClick = {
                    filters = listFilter[0]
                    filterState = SearchFilter.TASK
                    showDropDownMenu = false
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = listFilter[1],
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(6.dp)
                    )
                },
                onClick = {
                    filters = listFilter[1]
                    filterState = SearchFilter.NOTE
                    showDropDownMenu = false
                }
            )
        }
    }

    return filterState
}

@Composable
private fun myBottomBar(): String {
    var textFieldState by remember { mutableStateOf("") }
    val transparentColor = Color.Transparent

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        TextField(
            value = textFieldState,
            onValueChange = { textFieldState = it },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    0.4.dp, MaterialTheme.colorScheme.onSecondary,
                    MaterialTheme.shapes.small
                ),
            label = {
                if (textFieldState.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search) + "...",
                        color = colorResource(R.color.text_field_label_color),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            },
            shape = MaterialTheme.shapes.small,
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unfocusedIndicatorColor = transparentColor,
                focusedIndicatorColor = transparentColor,
                focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledContainerColor = transparentColor,
                cursorColor = MaterialTheme.colorScheme.onSecondary
            ),
            textStyle = MaterialTheme.typography.labelLarge
        )
    }

    return textFieldState
}