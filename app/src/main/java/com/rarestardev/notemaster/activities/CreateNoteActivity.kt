package com.rarestardev.notemaster.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.dao.NoteDao
import com.rarestardev.notemaster.database.AppDatabase
import com.rarestardev.notemaster.model.Note
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.view_model.NoteViewModel
import com.rarestardev.notemaster.view_model.NoteViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateNoteActivity : ComponentActivity() {

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(AppDatabase.getInstance(this).noteDao())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteMasterTheme {
                UiActivity(viewModel)
            }
        }
    }
}

@Preview
@Composable
fun PreviewAddNoteScreen() {
    val fakeDao = object : NoteDao {
        override suspend fun insertNote(note: Note) {}
        override fun getAllNotes(): Flow<List<Note>> = flowOf(emptyList())
    }
    val fakeViewModel = NoteViewModel(fakeDao)

    NoteMasterTheme { UiActivity(viewModel = fakeViewModel) }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun UiActivity(viewModel: NoteViewModel) {
    val iconTint = MaterialTheme.colorScheme.onSecondary
    val context = LocalContext.current

    var backActivity by remember { mutableStateOf(false) }

    Scaffold(
        Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_activity_create_note),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                },
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
            if (WindowInsets.isImeVisible) {
                MyBottomAppBar()
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding() + 6.dp,
                    start = 8.dp,
                    end = 8.dp
                )
                .fillMaxWidth()
        ) {
            NoteLayout(viewModel, backActivity)
        }
    }
}

@Preview
@Composable
private fun BottomBarPreview() {
    NoteMasterTheme {
        MyBottomAppBar()
    }
}

@SuppressLint("UseCompatLoadingForDrawables")
@Composable
private fun MyBottomAppBar() {
    val iconList = listOf(
        R.drawable.icon_bold,
        R.drawable.icons_text_color,
        R.drawable.icons_text_height,
        R.drawable.icons_rgb_color_wheel,
        R.drawable.icons_align_text_left
    )

    BottomAppBar(
        modifier = Modifier
            .imePadding()
            .padding(start = 10.dp, end = 10.dp, bottom = 6.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(0.5.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(8.dp)),

        tonalElevation = 12.dp,
        containerColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            items(iconList) { img ->
                IconButton(onClick = {
                    when (img) {
                        iconList[0] -> Log.d("ClickText", "0")
                        iconList[1] -> Log.d("ClickText", "1")
                        iconList[2] -> Log.d("ClickText", "2")
                        iconList[3] -> Log.d("ClickText", "3")
                        iconList[4] -> Log.d("ClickText", "4")
                    }
                }) {
                    Image(
                        painter = painterResource(img), contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteLayout(viewModel: NoteViewModel, backActivity: Boolean) {
    val activity = LocalContext.current as? Activity
    var textFieldTitle by remember {
        mutableStateOf(
            ""
        )
    }

    var textFieldType by remember { mutableStateOf("") }

    // textField title
    val transparentColor = Color.Transparent

    val context = LocalContext.current

    TextField(
        value = textFieldTitle,
        onValueChange = { textFieldTitle = it },
        label = {
            Text(
                stringResource(R.string.note_title),
                color = colorResource(R.color.text_field_label_color),
                style = TextStyle.Default.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textDirection = TextDirection.Content
                )
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.onSecondaryContainer, RoundedCornerShape(8.dp)
            ),
        leadingIcon = { Icon(imageVector = Icons.Default.AccountCircle, "") },
        trailingIcon = {
            IconButton(
                onClick = {
                    Toast.makeText(context,"Test flag", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.wrapContentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = stringResource(R.string.setflagonnote)
                )
            }
        },
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
    )// textField title

    Spacer(
        Modifier
            .fillMaxWidth()
            .height(12.dp)
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        val (textFieldRef, numTextRef, dateTextRef) = createRefs()

        TextField(
            value = textFieldType,
            onValueChange = { type2 ->
                textFieldType = type2
            },
            label = {
                Text(
                    stringResource(R.string.note_start_type),
                    color = colorResource(R.color.text_field_label_color),
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textDirection = TextDirection.Content
                    )
                )
            },
            modifier = Modifier
                .constrainAs(textFieldRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(dateTextRef.top)
                }
                .scrollable(state = rememberScrollState(), orientation = Orientation.Vertical)
                .fillMaxSize()
                .border(0.5.dp, MaterialTheme.colorScheme.onSecondaryContainer,RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = transparentColor,
                focusedContainerColor = transparentColor,
                disabledContainerColor = transparentColor,
                cursorColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedIndicatorColor = transparentColor,
                focusedIndicatorColor = transparentColor
            )
        ) // note text filed

        val currentTime = remember {
            SimpleDateFormat("yyyy/MM/dd - HH:mm:ss", Locale.getDefault()).format(Date())
        }

        Text(
            text = currentTime,
            modifier = Modifier
                .constrainAs(dateTextRef) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    top.linkTo(textFieldRef.bottom)
                }
                .padding(top = 12.dp),
            letterSpacing = 0.5.sp,
            fontSize = 12.sp,
            color = colorResource(R.color.text_field_label_color),
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "${textFieldType.length} Characters",
            modifier = Modifier
                .constrainAs(numTextRef) {
                    bottom.linkTo(dateTextRef.bottom)
                    end.linkTo(parent.end)
                    top.linkTo(textFieldRef.bottom)
                }
                .padding(top = 12.dp),
            letterSpacing = 0.5.sp,
            fontSize = 12.sp,
            color = colorResource(R.color.text_field_label_color),
            fontWeight = FontWeight.SemiBold
        )

        BackHandler {
            if (textFieldTitle.isEmpty()) {
                textFieldTitle = "Title"
            } else if (textFieldType.isNotEmpty()) {

                val note = Note(
                    title = textFieldTitle,
                    type = textFieldType,
                    timestamp = currentTime
                )

                viewModel.addNote(note)

                activity?.finish()
            }
        }

        if (backActivity) {
            if (textFieldType.isEmpty()) {
                activity?.finish()
            } else {
                if (textFieldTitle.isEmpty()) {
                    textFieldTitle = "no title"
                } else if (textFieldType.isNotEmpty()) {

                    val note = Note(
                        title = textFieldTitle,
                        type = textFieldType,
                        timestamp = currentTime
                    )

                    viewModel.addNote(note)

                    activity?.finish()
                }
            }
        }
    }
}