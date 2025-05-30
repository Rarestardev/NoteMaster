package com.rarestardev.notemaster.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.dao.NoteDao
import com.rarestardev.notemaster.database.AppDatabase
import com.rarestardev.notemaster.designs.DrawerContent
import com.rarestardev.notemaster.designs.FloatingActionMenu
import com.rarestardev.notemaster.feature.list.NoteLazyItem
import com.rarestardev.notemaster.feature.list.NoteLazyItemTitle
import com.rarestardev.notemaster.model.Note
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.view_model.NoteViewModel
import com.rarestardev.notemaster.view_model.NoteViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(AppDatabase.getInstance(this).noteDao())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteMasterTheme {
                HomeScreen(viewModel)
            }
        }
    }
}

@Preview
@Composable
fun PreviewNoteScreen() {
    val fakeDao = object : NoteDao {
        override suspend fun insertNote(note: Note) { }
        override fun getAllNotes(): Flow<List<Note>> = flowOf(
            listOf(
                Note(title = "Item 1", type = "Test 1", timestamp = "2025/05/29 - 12:00:00"),
                Note(title = "Item 2", type = "Test 2", timestamp = "2025/05/29 - 12:05:00")
            )
        )
    }
    val fakeViewModel = NoteViewModel(fakeDao)

    NoteMasterTheme { HomeScreen(fakeViewModel) }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun HomeScreen(viewModel: NoteViewModel) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val verticalScrollState = rememberScrollState()

    val notes by viewModel.allNotes.collectAsState(initial = emptyList())


    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        scrimColor = Color.Transparent,
        drawerContent = { DrawerContent(drawerState) }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            topBar = { MyTopAppBar(drawerState) },
            floatingActionButton = { FloatingActionMenu() },
            floatingActionButtonPosition = FabPosition.End
        ) {
            Column (
                modifier = Modifier.padding(
                    top = it.calculateTopPadding() + 10.dp,
                    start = dimensionResource(R.dimen.padding_start_end),
                    end = dimensionResource(R.dimen.padding_start_end)
                )
            ){

                NoteLazyItemTitle()

                LazyRow(
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notes) { note ->
                        Column {
                            NoteLazyItem(note)
                            Spacer(Modifier.width(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val showBadge by remember { mutableStateOf(false) }

    val iconTint = MaterialTheme.colorScheme.onSecondary

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        navigationIcon = {
            IconButton(onClick = {
                scope.launch { drawerState.open() }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.drawer_desc),
                    tint = iconTint
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        },
        actions = {
            IconButton(onClick = {
                val intentSearch = Intent(context, SearchActivity::class.java)
                context.startActivity(intentSearch)
            }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_desc),
                    tint = iconTint
                )
            }

            IconButton(onClick = {
                val intentNotification = Intent(context, NotificationActivity::class.java)
                context.startActivity(intentNotification)
            }) {
                BadgedBox(badge = {
                    if (showBadge) {
                        Badge(containerColor = Color.Red)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.notification_desc),
                        tint = iconTint
                    )
                }
            }
        }
    )
}