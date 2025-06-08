package com.rarestardev.notemaster.designs

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.dao.NoteDao
import com.rarestardev.notemaster.model.Note
import com.rarestardev.notemaster.note_editor.AddedBulletDot
import com.rarestardev.notemaster.note_editor.ColorPaletteDropMenu
import com.rarestardev.notemaster.note_editor.CustomSliderFontSize
import com.rarestardev.notemaster.note_editor.FontStyleDropMenu
import com.rarestardev.notemaster.note_editor.FontWeightDropMenu
import com.rarestardev.notemaster.note_editor.ShowDirectionTextDropMenu
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.view_model.NoteEditorViewModel
import com.rarestardev.notemaster.view_model.NoteViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Preview
@Composable
private fun Preview() {
    val fakeDao = object : NoteDao {
        override suspend fun insertNote(note: Note) {}
        override fun getAllNotes(): Flow<List<Note>> = flowOf(emptyList())
    }
    val fakeViewModel = NoteViewModel(fakeDao)
    NoteMasterTheme {
        NoteActivityDesign(NoteEditorViewModel())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun NoteActivityDesign(viewModel: NoteEditorViewModel) {
    val iconTint = MaterialTheme.colorScheme.onSecondary
    val context = LocalContext.current

    var backActivity by remember { mutableStateOf(false) }

    viewModel.updateColor(MaterialTheme.colorScheme.onPrimary)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TitleActivityText() },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!backActivity) backActivity = true
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_activity_desc),
                            tint = iconTint
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "More", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.back_activity_desc),
                            tint = iconTint
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .imePadding()
                    .padding(start = 10.dp, end = 10.dp, bottom = 6.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.onSecondary,
                        RoundedCornerShape(8.dp)
                    ),
                tonalElevation = 12.dp,
                containerColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                BottomAppBarContent(viewModel)
            }
        }
    ) { paddingValue ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValue.calculateTopPadding(),
                    bottom = paddingValue.calculateBottomPadding() + 6.dp,
                    start = 8.dp,
                    end = 8.dp
                )
        ) {
            textFiledTitle(
                Modifier
                    .fillMaxWidth()
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.onSecondaryContainer, RoundedCornerShape(8.dp)
                    )
            )

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )

            ConstraintLayout(
                modifier = Modifier
                    .padding(top = 26.dp, bottom = 20.dp)
                    .fillMaxSize()
            ) {
                val (textFieldNoteRef, layoutState) = createRefs()

                CustomTextFieldEditor(
                    modifier = Modifier
                        .constrainAs(textFieldNoteRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(layoutState.top, 12.dp)
                        }, viewModel = viewModel
                )

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(layoutState) {
                            top.linkTo(textFieldNoteRef.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom, 10.dp)
                        }
                ) {
                    val (numTextRef, dateTextRef) = createRefs()

                    SetTextCurrentTime(
                        modifier = Modifier
                            .constrainAs(dateTextRef) {
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                            }
                            .padding(top = 12.dp))

                    SetNumOfCharactersInTextFiledType(
                        viewModel.textState,
                        modifier = Modifier
                            .constrainAs(numTextRef) {
                                bottom.linkTo(dateTextRef.bottom)
                                end.linkTo(parent.end)
                            }
                            .padding(top = 12.dp))
                }
            }
        }
    }
}

@Composable
private fun textFiledTitle(modifier: Modifier): String {
    var textFieldTitle by remember { mutableStateOf("") }
    val transparentColor = Color.Transparent

    TextField(
        value = textFieldTitle,
        onValueChange = { textFieldTitle = it },
        label = { LabelText(stringResource(R.string.note_title)) },
        modifier = modifier,
        leadingIcon = {
            Image(painter = painterResource(R.drawable.icons_title), "NoteTitleIcon")
        },
        trailingIcon = { ShowPopupMenuPriority() },
        minLines = 1,
        maxLines = 2,
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = transparentColor,
            focusedContainerColor = transparentColor,
            disabledContainerColor = transparentColor,
            cursorColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedIndicatorColor = transparentColor,
            focusedIndicatorColor = transparentColor
        )
    )

    return textFieldTitle
}


@SuppressLint("MutableCollectionMutableState")
@Composable
private fun CustomTextFieldEditor(
    viewModel: NoteEditorViewModel,
    modifier: Modifier
) {
    val transparentColor = Color.Transparent

    ConstraintLayout(modifier = modifier) {

        TextField(
            value = viewModel.textState,
            onValueChange = { newText -> viewModel.onTextChanged(newText) },
            modifier = Modifier
                .constrainAs(createRef()) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .scrollable(
                    state = rememberScrollState(),
                    orientation = Orientation.Vertical
                )
                .fillMaxSize()
                .border(
                    0.5.dp,
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    RoundedCornerShape(8.dp)
                )
                .fillMaxSize(),
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = transparentColor,
                focusedContainerColor = transparentColor,
                disabledContainerColor = transparentColor,
                cursorColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedIndicatorColor = transparentColor,
                focusedIndicatorColor = transparentColor
            ),
            textStyle = TextStyle(
                fontSize = viewModel.textSizeSliderValue,
                textAlign = viewModel.directionText,
                color = viewModel.selectedColor,
                fontWeight = viewModel.fontWeightValue,
                fontStyle = viewModel.fontStyleValue
            ),
            label = {
                if (viewModel.textState.text.isEmpty()) {
                    LabelText(stringResource(R.string.note_start_type))
                }
            }
        )
    }
}

@Composable
private fun LabelText(title: String) {
    Text(
        text = title,
        color = colorResource(R.color.text_field_label_color),
        style = TextStyle.Default.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textDirection = TextDirection.Content
        )
    )
}

@Composable
private fun BottomAppBarContent(viewModel: NoteEditorViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        FontWeightDropMenu(viewModel)

        ColorPaletteDropMenu(viewModel)

        ShowDirectionTextDropMenu(viewModel)

        CustomSliderFontSize(viewModel)

        AddedBulletDot(viewModel)

        FontStyleDropMenu(viewModel)
    }
}

@Composable
private fun TitleActivityText() {
    Text(
        text = stringResource(R.string.title_activity_create_note),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun SetNumOfCharactersInTextFiledType(textFieldType: TextFieldValue, modifier: Modifier) {
    Text(
        text = "${textFieldType.text.length} Characters",
        modifier = modifier,
        letterSpacing = 0.5.sp,
        fontSize = 12.sp,
        color = colorResource(R.color.text_field_label_color),
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SetTextCurrentTime(modifier: Modifier) {
    Text(
        text = currentTime(),
        modifier = modifier,
        letterSpacing = 0.5.sp,
        fontSize = 12.sp,
        color = colorResource(R.color.text_field_label_color),
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun currentTime(): String {
    return remember {
        SimpleDateFormat(
            "yyyy/MM/dd - HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
    }
}