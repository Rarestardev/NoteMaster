package com.rarestardev.notemaster.components

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
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
import androidx.constraintlayout.compose.Dimension
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.activities.CreateNoteActivity
import com.rarestardev.notemaster.activities.ShowAllNotesActivity
import com.rarestardev.notemaster.model.Flags
import com.rarestardev.notemaster.model.ImageResource
import com.rarestardev.notemaster.model.Note
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.utilities.Constants
import com.rarestardev.notemaster.utilities.previewFakeViewModel
import com.rarestardev.notemaster.view_model.NoteEditorViewModel

@Preview
@Composable
private fun NoteScreenPreview() {
    NoteMasterTheme(
        darkTheme = true
    ) {
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 12.dp)
            )

            if (notes.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.see_more),
                    style = MaterialTheme.typography.labelSmall,
                    color = colorResource(R.color.text_field_label_color),
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(context, ShowAllNotesActivity::class.java).apply {
                                putExtra(Constants.STATE_NOTE_PRIORITY_ACTIVITY,false)
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
                                Text(
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

                            if (isShowMenuBottomSheet) {
                                MenuBottomSheet(
                                    noteEditorViewModel = viewModel,
                                    id = note.id,
                                    note = note
                                ) {
                                    isShowMenuBottomSheet = it
                                }
                            }
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
                text = "Notes list is empty ...",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(top = 50.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBottomSheet(
    noteEditorViewModel: NoteEditorViewModel,
    id: Int,
    note: Note,
    onDismiss: (Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
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
    var priorityFlagShowPage by remember { mutableStateOf(false) }

    BackHandler {
        if (priorityFlagShowPage) priorityFlagShowPage = false
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss(false) },
        sheetState = sheetState,
        scrimColor = Color.Transparent,
        containerColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        if (!priorityFlagShowPage) {
            BottomSheetItems(
                menuItem[0],
                ImageResource.Vector(Icons.Default.Delete)
            ) {
                noteEditorViewModel.deleteNote(note)
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

                                noteEditorViewModel.updatePriorityFlagInDatabase(flagIndex, id)
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