package com.rarestardev.notemaster.designs

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
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
import com.rarestardev.notemaster.model.StyledSegment
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.utilities.Constants
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
        NoteActivityDesign()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun NoteActivityDesign() {
    val iconTint = MaterialTheme.colorScheme.onSecondary
    val context = LocalContext.current

    var backActivity by remember { mutableStateOf(false) }
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var styledSegments by remember { mutableStateOf(mutableListOf<StyledSegment>()) }
    var isBoldActive by remember { mutableStateOf(false) }
    var isColorActive by remember { mutableStateOf(false) }
    var lastStyledIndex by remember { mutableIntStateOf(0) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var isPaletteVisible by remember { mutableStateOf(false) }
    var textSize by remember { mutableFloatStateOf(18f) }
    var isTextSelected by remember { mutableStateOf(false) }
    var isFontListVisible by remember { mutableStateOf(false) }
    var isFontStyleVisible by remember { mutableStateOf(false) }

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
                BottomAppBarContent()
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
                    textState = textState,
                    onChangedTextFieldValue = { newText ->
                        val selection = textState.selection
                        if (selection.min < selection.max) {
                            styledSegments.add(
                                StyledSegment(
                                    selection.min,
                                    selection.max,
                                    SpanStyle(fontWeight = FontWeight.Bold)
                                )
                            )
                        } else {
                            Log.e(Constants.APP_LOG, "not choose text with user wait for selected text and update style")
                        }

                        isTextSelected = newText.selection.min != newText.selection.max
                        if (newText.text.length > textState.text.length) {
                            lastStyledIndex = textState.text.length
                        }
                        textState = newText.copy(
                            annotatedString = applyStylesToText(
                                newText.text,
                                styledSegments,
                                isBoldActive,
                                isColorActive,
                                lastStyledIndex
                            )
                        )
                    },
                    textSize = textSize,
                    isTextSelected = isTextSelected,
                    isFontListVisible = { isFontListVisible = !isFontListVisible },
                    isFontStyleVisible = { isFontStyleVisible = !isFontStyleVisible },
                    isPaletteVisible = { isPaletteVisible = !isPaletteVisible },
                    selectedColor = selectedColor,
                    fontWeight = {
                        if (isFontStyleVisible) isFontStyleVisible = false
                        if (isPaletteVisible) isPaletteVisible = false
                        SelectedTextFontWeight { style ->
                            applyStyle(styledSegments, textState, SpanStyle(fontWeight = style))
                            textState = textState.copy(
                                annotatedString = applyStylesToText(
                                    textState.text,
                                    styledSegments,
                                    isBoldActive,
                                    isColorActive,
                                    lastStyledIndex
                                )
                            )
                            isFontListVisible = false
                        }
                    },
                    fontStyle = {
                        if (isFontListVisible) isFontListVisible = false
                        if (isPaletteVisible) isPaletteVisible = false
                        SelectedTextFontStyle { style ->
                            applyStyle(styledSegments, textState, SpanStyle(fontStyle = style))
                            textState = textState.copy(
                                annotatedString = applyStylesToText(
                                    textState.text,
                                    styledSegments,
                                    isBoldActive,
                                    isColorActive,
                                    lastStyledIndex
                                )
                            )
                            isFontStyleVisible = false
                        }
                    },
                    palette = {
                        if (isFontListVisible) isFontListVisible = false
                        if (isFontStyleVisible) isFontStyleVisible = false

                        selectedColor = it
                        isPaletteVisible = false
                    },
                    isFontListVisibleValue = isFontListVisible,
                    isFontStyleVisibleValue = isFontStyleVisible,
                    isPaletteVisibleValue = isPaletteVisible,
                    modifier = Modifier
                        .constrainAs(textFieldNoteRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(layoutState.top, 12.dp)
                        }
                )

                ConstraintLayout (
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(layoutState) {
                            top.linkTo(textFieldNoteRef.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom, 10.dp)
                        }
                ){
                    val (textFieldRef, numTextRef, dateTextRef) = createRefs()

                    SetTextCurrentTime(
                        modifier = Modifier
                            .constrainAs(dateTextRef) {
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                top.linkTo(textFieldRef.bottom)
                            }
                            .padding(top = 12.dp))

                    SetNumOfCharactersInTextFiledType(
                        textState,
                        modifier = Modifier
                            .constrainAs(numTextRef) {
                                bottom.linkTo(dateTextRef.bottom)
                                end.linkTo(parent.end)
                                top.linkTo(textFieldRef.bottom)
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
    textState: TextFieldValue,
    onChangedTextFieldValue: (TextFieldValue) -> Unit,
    textSize: Float,
    isTextSelected: Boolean,
    isFontListVisible: (Boolean) -> Unit,
    isFontStyleVisible: (Boolean) -> Unit,
    isPaletteVisible: (Boolean) -> Unit,
    selectedColor: Color,
    fontWeight: @Composable () -> Unit,
    fontStyle: @Composable () -> Unit,
    palette: (Color) -> Unit,
    isFontListVisibleValue: Boolean,
    isFontStyleVisibleValue: Boolean,
    isPaletteVisibleValue: Boolean,
    modifier: Modifier
) {
    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta)
    val transparentColor = Color.Transparent

    ConstraintLayout(modifier = modifier) {
        val (textFieldNoteRef, editableLayoutRef, layoutFontStyleRef, colorPaletteRef) = createRefs()

        TextField(
            value = textState,
            onValueChange = { newText -> onChangedTextFieldValue(newText) },
            modifier = Modifier
                .constrainAs(textFieldNoteRef) {
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
            textStyle = TextStyle(fontSize = textSize.sp),
            label = {
                if (textState.text.isEmpty()){
                    LabelText(stringResource(R.string.note_start_type))
                }
            }
        )

        if (isTextSelected) {
            EditableTextField(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                    .background(MaterialTheme.colorScheme.onSecondary)
                    .padding(6.dp)
                    .constrainAs(editableLayoutRef) {
                        top.linkTo(textFieldNoteRef.top)
                        bottom.linkTo(textFieldNoteRef.bottom)
                        end.linkTo(textFieldNoteRef.end)
                    },
                isFontListVisible = { isFontListVisible },
                isFontStyleVisible = { isFontStyleVisible },
                isPaletteVisible = { isPaletteVisible },
                selectedColor = selectedColor
            )
        }

        AnimatedVisibility(
            isFontStyleVisibleValue,
            modifier = Modifier
                .constrainAs(layoutFontStyleRef) {
                    top.linkTo(editableLayoutRef.top)
                    bottom.linkTo(editableLayoutRef.bottom)
                    end.linkTo(editableLayoutRef.start)
                }
                .padding(end = 4.dp)
        ) { fontStyle }

        AnimatedVisibility(
            isFontListVisibleValue,
            modifier = Modifier
                .imePadding()
                .constrainAs(createRef()) {
                    top.linkTo(editableLayoutRef.top)
                    bottom.linkTo(editableLayoutRef.bottom)
                    end.linkTo(editableLayoutRef.start)
                }
                .padding(end = 4.dp)
        ) { fontWeight }

        AnimatedVisibility(isPaletteVisibleValue) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .width(70.dp)
                    .padding(end = 4.dp)
                    .constrainAs(colorPaletteRef) {
                        top.linkTo(editableLayoutRef.top)
                        bottom.linkTo(editableLayoutRef.bottom)
                        end.linkTo(editableLayoutRef.start)
                    }
                    .border(
                        0.3.dp,
                        MaterialTheme.colorScheme.onSecondary,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                items(colors) { color ->
                    Box(
                        Modifier
                            .size(30.dp)
                            .background(color, RoundedCornerShape(8.dp))
                            .padding(1.dp)
                            .clickable {
                                palette(color)
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun EditableTextField(
    modifier: Modifier,
    isFontListVisible: () -> Unit,
    isFontStyleVisible: () -> Unit,
    isPaletteVisible: () -> Unit,
    selectedColor: Color
) {
    Column(
        modifier = modifier,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = {
            isFontListVisible
        }) {
            Icon(
                painter = painterResource(R.drawable.icon_bold),
                contentDescription = "Bold",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        IconButton(onClick = {
            isFontStyleVisible
        }) {
            Icon(
                painter = painterResource(R.drawable.icons_text_height),
                contentDescription = "FontStyle",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        Box(
            Modifier
                .padding(top = 6.dp, bottom = 8.dp)
                .clickable {
                    isPaletteVisible
                }
        ) {
            Canvas(
                modifier = Modifier.size(16.dp)
            ) {
                drawCircle(color = selectedColor, size.minDimension / 2)
            }
        }
    }
}

@Composable
private fun SelectedTextFontWeight(fontWeight: (FontWeight) -> Unit) {
    val fonts = listOf(
        "SemiBold" to FontWeight.SemiBold,
        "Bold" to FontWeight.Bold,
        "ExtraBold" to FontWeight.ExtraBold,
        "Normal" to FontWeight.Normal,
        "Medium" to FontWeight.Medium,
        "Light" to FontWeight.Light,
        "ExtraLight" to FontWeight.ExtraLight
    )
    Column(
        modifier = Modifier
            .width(80.dp)
            .border(0.5.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.onSecondaryContainer),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        fonts.forEach { style ->
            Text(
                text = style.first,
                fontWeight = style.second,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        fontWeight(style.second)
                    }
            )
            Spacer(
                modifier = Modifier
                    .height(0.3.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSecondary)
            )
        }
    }
}

@Composable
private fun SelectedTextFontStyle(fontStyle: (FontStyle) -> Unit) {
    val fonts = listOf(
        "Italic" to FontStyle.Italic,
        "Normal" to FontStyle.Normal
    )
    Column(
        modifier = Modifier
            .width(80.dp)
            .border(0.5.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.onSecondaryContainer),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        fonts.forEach { style ->
            Text(
                text = style.first,
                fontWeight = FontWeight.Normal,
                fontStyle = style.second,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        fontStyle(style.second)
                    }
            )
            Spacer(
                modifier = Modifier
                    .height(0.3.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSecondary)
            )
        }
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
private fun BottomAppBarContent() {
    Box {

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

private fun addBulletToSelectedLine(
    textFieldValue: TextFieldValue,
    bulletValue: String
): TextFieldValue {
    val text = textFieldValue.text
    val cursorPosition = textFieldValue.selection.start

    val lines = text.lines().toMutableList()
    var currentLineIndex = 0
    var charCount = 0

    for ((index, line) in lines.withIndex()) {
        charCount += line.length + 1

        if (cursorPosition <= charCount) {
            currentLineIndex = index
            break
        }
    }

    lines[currentLineIndex] = bulletValue + lines[currentLineIndex]

    return textFieldValue.copy(text = lines.joinToString("\n"))
}

private fun applyStylesToText(
    text: String,
    styledSegments: MutableList<StyledSegment>,
    isBoldActive: Boolean,
    isColorActive: Boolean,
    lastStyledIndex: Int
): AnnotatedString {
    return buildAnnotatedString {
        append(text)

        styledSegments.forEach { segment ->
            if (segment.end <= text.length) {
                addStyle(segment.style, segment.start, segment.end)
            }
        }

        if (isBoldActive) {
            val newSegment =
                StyledSegment(lastStyledIndex, text.length, SpanStyle(fontWeight = FontWeight.Bold))
            if (lastStyledIndex in 0..text.length) {
                addStyle(newSegment.style, newSegment.start, newSegment.end)
                styledSegments.add(newSegment)
            }
        }

        if (isColorActive) {
            val newSegment =
                StyledSegment(lastStyledIndex, text.length, SpanStyle(color = Color.Blue))
            if (lastStyledIndex in 0..text.length) {
                addStyle(newSegment.style, newSegment.start, newSegment.end)
                styledSegments.add(newSegment)
            }
        }
    }
}

private fun applyStyle(
    styledSegments: MutableList<StyledSegment>,
    textFieldValue: TextFieldValue,
    style: SpanStyle
) {
    val selection = textFieldValue.selection
    if (selection.min != selection.max) {
        styledSegments.add(StyledSegment(selection.min, selection.max, style))
    }
}