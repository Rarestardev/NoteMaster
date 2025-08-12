package com.rarestardev.taskora.components

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rarestardev.taskora.R
import com.rarestardev.taskora.model.Flags
import com.rarestardev.taskora.model.ImageResource
import com.rarestardev.taskora.model.Note
import com.rarestardev.taskora.view_model.NoteEditorViewModel

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBottomSheet(
    noteEditorViewModel: NoteEditorViewModel,
    note: Note,
    onDismiss: (Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
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
                onDismiss(false)
            }

            BottomSheetItems(
                menuItem[1],
                ImageResource.Vector(Icons.Default.Share)
            ) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_SUBJECT,note.noteTitle)
                    putExtra(Intent.EXTRA_TEXT, note.noteText)
                    type = "text/plain"
                }

                val chooser = Intent.createChooser(shareIntent,note.noteTitle)
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

                                noteEditorViewModel.updatePriorityFlagInDatabase(flagIndex, note.id)
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