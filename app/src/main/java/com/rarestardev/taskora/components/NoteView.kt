package com.rarestardev.taskora.components

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.rarestardev.taskora.activities.CreateNoteActivity
import com.rarestardev.taskora.activities.ShowAllNotesActivity
import com.rarestardev.taskora.feature.CustomText
import com.rarestardev.taskora.model.Note
import com.rarestardev.taskora.ui.theme.TaskoraTheme
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.utilities.LanguageHelper
import com.rarestardev.taskora.utilities.previewFakeViewModel
import com.rarestardev.taskora.view_model.NoteEditorViewModel

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */
@Preview
@Composable
private fun NoteScreenPreview() {
    TaskoraTheme {
        NoteScreen(previewFakeViewModel())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteScreen(viewModel: NoteEditorViewModel) {
    val notes by viewModel.allNote.collectAsState(initial = emptyList())
    val context = LocalContext.current
    var isShowMenuBottomSheet by remember { mutableStateOf(false) }
    var noteModel by remember { mutableStateOf(Note()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.note),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 12.dp),
                maxLines = 1,
                textAlign = TextAlign.Start
            )

            if (notes.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.see_more),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    color = colorResource(R.color.text_field_label_color),
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(context, ShowAllNotesActivity::class.java).apply {
                                putExtra(Constants.STATE_NOTE_PRIORITY_ACTIVITY, false)
                            }
                            context.startActivity(intent)
                        }
                        .padding(end = 12.dp)
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        if (notes.isNotEmpty()) {
            LazyRow(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(notes) { note ->
                    var flagColor by remember { mutableIntStateOf(R.color.priority_low) }

                    when (note.priority) {
                        0 -> flagColor = R.color.priority_low
                        1 -> flagColor = R.color.priority_medium
                        2 -> flagColor = R.color.priority_high
                    }

                    ConstraintLayout(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(top = 8.dp)
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
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = if (LanguageHelper.isPersian(note.noteText)) {
                                        TextAlign.Right
                                    } else {
                                        TextAlign.Left
                                    }
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
                                        MaterialTheme.shapes.extraSmall
                                    )
                                    .border(
                                        0.2.dp, MaterialTheme.colorScheme.onSecondary,
                                        MaterialTheme.shapes.extraSmall
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
                                    textAlign = if (LanguageHelper.isPersian(note.noteTitle)) {
                                        TextAlign.Right
                                    } else {
                                        TextAlign.Left
                                    }
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
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }

        if (isShowMenuBottomSheet) {
            MenuBottomSheet(
                noteEditorViewModel = viewModel,
                note = noteModel
            ) {
                isShowMenuBottomSheet = it
            }
        }
    }
}