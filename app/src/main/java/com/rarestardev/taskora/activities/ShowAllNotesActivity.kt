package com.rarestardev.taskora.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.rarestardev.taskora.R
import com.rarestardev.taskora.components.BannerAds
import com.rarestardev.taskora.components.MenuBottomSheet
import com.rarestardev.taskora.database.NoteDatabase
import com.rarestardev.taskora.factory.NoteViewModelFactory
import com.rarestardev.taskora.feature.CustomText
import com.rarestardev.taskora.model.Note
import com.rarestardev.taskora.ui.theme.NoteMasterTheme
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.utilities.previewFakeViewModel
import com.rarestardev.taskora.view_model.NoteEditorViewModel

class ShowAllNotesActivity : BaseActivity() {

    private val viewModel: NoteEditorViewModel by viewModels {
        NoteViewModelFactory(NoteDatabase.getInstance(this).noteDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val state = intent.getBooleanExtra(Constants.STATE_NOTE_PRIORITY_ACTIVITY, false)

        setComposeContent {
            ActivityScreen(viewModel, state)
        }
    }
}

@Preview
@Composable
private fun NoteScreenPreview() {
    NoteMasterTheme {
        ActivityScreen(previewFakeViewModel(), false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun ActivityScreen(viewModel: NoteEditorViewModel, state: Boolean) {
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.note),
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
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp),
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    BannerAds()
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(
                top = it.calculateTopPadding() + 8.dp,
                start = 12.dp,
                end = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NoteScreen(viewModel, state)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun NoteScreen(viewModel: NoteEditorViewModel, state: Boolean) {
    val notes by viewModel.allNote.collectAsState(initial = emptyList())
    val context = LocalContext.current
    val lazyGridState = rememberLazyGridState()
    var isShowMenuBottomSheet by remember { mutableStateOf(false) }
    var noteModel by remember { mutableStateOf(Note(0,"","",0,"","",0,0f)) }

    val filterNote = if (state) {
        notes.filter { it.priority == 2 }.sortedByDescending { it.priority }
    } else {
        notes
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (notes.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = lazyGridState,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(filterNote) { note ->
                    var flagColor by remember { mutableIntStateOf(R.color.priority_low) }

                    when (note.priority) {
                        0 -> flagColor = R.color.priority_low
                        1 -> flagColor = R.color.priority_medium
                        2 -> flagColor = R.color.priority_high
                    }

                    ConstraintLayout(
                        modifier = Modifier
                            .wrapContentSize()
                            .combinedClickable(
                                onClick = {
                                    val previewNoteIntent =
                                        Intent(context, CreateNoteActivity::class.java)
                                            .putExtra("noteId", note.id)
                                            .putExtra("noteTitle", note.noteTitle)
                                            .putExtra("noteText", note.noteText)
                                            .putExtra("priority", note.priority)
                                            .putExtra("timeStamp", note.timeStamp)
                                            .putExtra("date", note.date)
                                            .putExtra("fontWeight", note.fontWeight)
                                            .putExtra("fontSize", note.fontSize)
                                            .putExtra(Constants.STATE_NOTE_ACTIVITY, true)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                                    context.startActivity(previewNoteIntent)
                                },
                                onLongClick = {
                                    isShowMenuBottomSheet = true
                                    noteModel = note
                                }
                            )
                    ) {
                        val (noteBoxRef, titleRef) = createRefs()

                        if (notes.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(140.dp)
                                    .background(
                                        MaterialTheme.colorScheme.background,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        0.5.dp,
                                        colorResource(flagColor),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .constrainAs(noteBoxRef) {
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                        bottom.linkTo(parent.bottom)
                                        top.linkTo(parent.top)
                                    }
                            ) {
                                Text(
                                    text = note.noteText,
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .constrainAs(titleRef) {
                                        start.linkTo(noteBoxRef.start)
                                        end.linkTo(noteBoxRef.end)
                                        bottom.linkTo(noteBoxRef.top, (-10).dp)
                                    }
                                    .background(
                                        MaterialTheme.colorScheme.onSecondaryContainer,
                                        RoundedCornerShape(4.dp)
                                    )

                            ) {
                                Text(
                                    text = note.noteTitle,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    color = colorResource(flagColor),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .wrapContentWidth()
                                    .background(
                                        MaterialTheme.colorScheme.onSecondaryContainer,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .constrainAs(createRef()) {
                                        end.linkTo(noteBoxRef.end, 2.dp)
                                        bottom.linkTo(noteBoxRef.bottom, 1.dp)
                                    }
                            ) {
                                CustomText(
                                    text = note.date + " - " + note.timeStamp,
                                    modifier = Modifier
                                        .padding(
                                            start = 4.dp,
                                            end = 4.dp
                                        ),
                                    color = colorResource(flagColor),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Icon(
                                painter = painterResource(R.drawable.icons_flag),
                                contentDescription = note.noteTitle,
                                modifier = Modifier
                                    .size(8.dp)
                                    .constrainAs(createRef()) {
                                        start.linkTo(noteBoxRef.start, 8.dp)
                                        bottom.linkTo(noteBoxRef.bottom, 4.dp)
                                    },
                                tint = colorResource(flagColor)
                            )

                        } else {
                            Text(
                                text = stringResource(R.string.no_note),
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.constrainAs(createRef()) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top, 60.dp)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(parent.bottom)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        } else {
            Text(
                text = stringResource(R.string.notes_list_is_empty),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(top = 50.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center
            )
        }

        if (isShowMenuBottomSheet){
            MenuBottomSheet(
                noteEditorViewModel = viewModel,
                note = noteModel
            ) {
                isShowMenuBottomSheet = it
            }
        }
    }
}