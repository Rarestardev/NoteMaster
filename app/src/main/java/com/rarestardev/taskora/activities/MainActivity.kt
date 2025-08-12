package com.rarestardev.taskora.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rarestardev.taskora.R
import com.rarestardev.taskora.components.AdiveryNativeAdLayoutWithTitle
import com.rarestardev.taskora.components.BannerAds
import com.rarestardev.taskora.components.CategoryListView
import com.rarestardev.taskora.components.CircularTaskStatusBar
import com.rarestardev.taskora.components.CompleteTaskView
import com.rarestardev.taskora.components.DailyTaskProgress
import com.rarestardev.taskora.components.HorizontalPagerView
import com.rarestardev.taskora.components.NoteScreen
import com.rarestardev.taskora.components.TaskView
import com.rarestardev.taskora.database.NoteDatabase
import com.rarestardev.taskora.factory.NoteViewModelFactory
import com.rarestardev.taskora.factory.SubTaskViewModelFactory
import com.rarestardev.taskora.factory.TaskViewModelFactory
import com.rarestardev.taskora.feature.CustomText
import com.rarestardev.taskora.service.ReminderService
import com.rarestardev.taskora.ui.theme.TaskoraTheme
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.utilities.previewFakeTaskViewModel
import com.rarestardev.taskora.utilities.previewFakeViewModel
import com.rarestardev.taskora.utilities.previewSubTaskViewModel
import com.rarestardev.taskora.view_model.NoteEditorViewModel
import com.rarestardev.taskora.view_model.SubTaskViewModel
import com.rarestardev.taskora.view_model.TaskViewModel

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */
class MainActivity : BaseActivity() {

    private val viewModel: NoteEditorViewModel by viewModels {
        NoteViewModelFactory(NoteDatabase.getInstance(this).noteDao())
    }

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(NoteDatabase.getInstance(this).taskItemDao())
    }

    private val subTaskViewModel: SubTaskViewModel by viewModels {
        SubTaskViewModelFactory(NoteDatabase.getInstance(this).subTaskDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setComposeContent {
            HomeScreen(viewModel, taskViewModel, subTaskViewModel)
        }
    }

    override fun onResume() {
        super.onResume()

        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
        ringtone?.let {
            if (it.isPlaying){
                val br = Intent(this@MainActivity, ReminderService::class.java)
                    .setAction(Constants.CANCEL_ALARM)

                sendBroadcast(br)
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    TaskoraTheme {
        HomeScreen(
            previewFakeViewModel(),
            previewFakeTaskViewModel(),
            previewSubTaskViewModel()
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun HomeScreen(
    viewModel: NoteEditorViewModel,
    taskViewModel: TaskViewModel,
    subTaskViewModel: SubTaskViewModel
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val mContext = LocalContext.current

    var expanded by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = expanded, label = "transition")
    val rotation by transition.animateFloat(label = "rotation") {
        if (it) 315f else 0f
    }

    val items = listOf(
        MiniFabItems(R.drawable.icon_note, stringResource(R.string.note)),
        MiniFabItems(R.drawable.icons_checklist, stringResource(R.string.task_bottom_bar))
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        topBar = { MyTopAppBar() },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }) + expandVertically(),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it }) + shrinkVertically()
                ) {
                    LazyColumn(
                        state = rememberLazyListState(),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        itemsIndexed(items) { index, miniFab ->
                            ExpandedFabItems(miniFab) { b ->
                                if (index == 0) {
                                    val createNoteIntent =
                                        Intent(mContext, CreateNoteActivity::class.java)
                                    createNoteIntent.putExtra(Constants.STATE_NOTE_ACTIVITY, false)
                                    createNoteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    mContext.startActivity(createNoteIntent)
                                    expanded = b
                                } else if (index == 1) {
                                    val createTaskIntent =
                                        Intent(mContext, CreateTaskActivity::class.java)
                                    createTaskIntent.putExtra(Constants.STATE_TASK_ACTIVITY, false)
                                    createTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    mContext.startActivity(createTaskIntent)
                                    expanded = b
                                }
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = {
                        expanded = !expanded
                    },
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.title_activity_create_note),
                        modifier = Modifier.rotate(rotation),
                        tint = Color.White
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->

        ScaffoldContent(paddingValues, viewModel, taskViewModel, subTaskViewModel)
    }
}

@Composable
private fun ScaffoldContent(
    paddingValues: PaddingValues,
    viewModel: NoteEditorViewModel,
    taskViewModel: TaskViewModel,
    subTaskViewModel: SubTaskViewModel
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        TopTaskProgress(viewModel, taskViewModel)

        BannerAds()

        TaskView(taskViewModel, subTaskViewModel)

        BannerAds()

        NoteScreen(viewModel)

        BannerAds()

        CompleteTaskView(taskViewModel, subTaskViewModel)

        BannerAds()

        CategoryListView()

        AdiveryNativeAdLayoutWithTitle(true)

        Spacer(modifier = Modifier.height(65.dp))
    }
}

@Composable
private fun TopTaskProgress(
    noteViewModel: NoteEditorViewModel,
    taskViewModel: TaskViewModel
) {
    val notes by noteViewModel.allNote.collectAsState(emptyList())
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(
                start = 12.dp,
                end = 12.dp
            )
    ) {
        val (highPriorityLayoutRef, inProgressRef,dividerRef) = createRefs()

        HorizontalPagerView(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onSecondary, MaterialTheme.shapes.small)
                .constrainAs(highPriorityLayoutRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(dividerRef.start,6.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
            taskViewModel,
            noteViewModel
        )

        VerticalDivider(
            modifier = Modifier.constrainAs(dividerRef){
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
            color = Color.Transparent
        )

        Column (
            modifier = Modifier
                .constrainAs(inProgressRef) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(dividerRef.end,6.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            UserStateProgress(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        MaterialTheme.shapes.small
                    )
                    .border(
                        0.3.dp, MaterialTheme.colorScheme.onSecondary,
                        MaterialTheme.shapes.small
                    )
                    .fillMaxWidth(),
                taskViewModel
            )

            Row(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        MaterialTheme.shapes.small
                    )
                    .border(
                        0.3.dp, MaterialTheme.colorScheme.onSecondary,
                        MaterialTheme.shapes.small
                    )
                    .clickable {
                        val intent = Intent(context, ShowAllNotesActivity::class.java).apply {
                            putExtra(Constants.STATE_NOTE_PRIORITY_ACTIVITY, false)
                        }
                        context.startActivity(intent)
                    }
                    .height(50.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_note),
                    contentDescription = stringResource(R.string.note),
                    tint = MaterialTheme.colorScheme.onPrimary
                )

                CustomText(
                    text = stringResource(R.string.all_notes) + " ( " + notes.size + " )",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }

            DailyTaskProgress(
                modifier = Modifier
                    .clickable {
                        context.startActivity(Intent(context, CalenderActivity::class.java))
                    }
                    .height(50.dp)
                    .fillMaxWidth(),
                taskViewModel = taskViewModel
            )
        }
    }
}

@Composable
private fun UserStateProgress(modifier: Modifier = Modifier, taskViewModel: TaskViewModel) {
    val taskElement by taskViewModel.taskElement.collectAsState(emptyList())
    ConstraintLayout(
        modifier = modifier.padding(12.dp)
    ) {
        val (progressRef, descLayoutRef, inProgressLayoutRef) = createRefs()
        val completeTask = taskElement.filter { it.isComplete == true }
            .sortedByDescending { it.isComplete }.size
        val totalTaskListSize = taskElement.size
        val inProgress = totalTaskListSize - completeTask

        CircularTaskStatusBar(
            totalTask = totalTaskListSize,
            complete = completeTask,
            modifier = Modifier.constrainAs(progressRef) {
                start.linkTo(parent.start, 4.dp)
                top.linkTo(descLayoutRef.top)
                bottom.linkTo(inProgressLayoutRef.bottom)
            }
        )

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
                .padding(4.dp)
                .constrainAs(descLayoutRef) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    start.linkTo(progressRef.end, 12.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(R.string.completed),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = 2.dp,
                        bottom = 2.dp
                    ),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )

            CustomText(
                text = "$completeTask / $totalTaskListSize" + " " + stringResource(R.string.task_bottom_bar),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = 2.dp,
                        bottom = 2.dp
                    ),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
                .padding(4.dp)
                .constrainAs(inProgressLayoutRef) {
                    end.linkTo(parent.end)
                    top.linkTo(descLayoutRef.bottom, 6.dp)
                    start.linkTo(progressRef.end, 12.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(R.string.in_progress),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = 2.dp,
                        bottom = 2.dp
                    ),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )

            CustomText(
                text = "$inProgress" + " " + stringResource(R.string.progress) ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = 2.dp,
                        bottom = 2.dp
                    ),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTopAppBar() {
    val context = LocalContext.current

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        actions = {
            IconButton(
                onClick = {
                    context.startActivity(Intent(context, CalenderActivity::class.java))
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons_calendar),
                    contentDescription = stringResource(R.string.calender_bottom_bar),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(
                onClick = {
                    context.startActivity(Intent(context, SearchActivity::class.java))
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_desc),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(
                onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_bottom_bar),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

data class MiniFabItems(
    val icon: Int,
    val title: String
)

@Composable
private fun ExpandedFabItems(miniFab: MiniFabItems, onClick: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .width(100.dp)
            .background(MaterialTheme.colorScheme.onSecondaryContainer, RoundedCornerShape(12.dp))
            .padding(6.dp)
            .clickable { onClick(false) }
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = miniFab.title,
            modifier = Modifier
                .width(50.dp)
                .padding(4.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(miniFab.icon),
            contentDescription = miniFab.title,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.onSecondary,
                    RoundedCornerShape(6.dp)
                )
                .padding(4.dp)
                .size(20.dp),
            tint = Color.White
        )
    }

    Spacer(Modifier.height(8.dp))
}