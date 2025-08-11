package com.rarestardev.taskora.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.components.BannerAds
import com.rarestardev.taskora.database.NoteDatabase
import com.rarestardev.taskora.factory.SubTaskViewModelFactory
import com.rarestardev.taskora.factory.TaskViewModelFactory
import com.rarestardev.taskora.list_item.TaskLazyItems
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.view_model.SubTaskViewModel
import com.rarestardev.taskora.view_model.TaskViewModel

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */
class ShowAllTasksActivity : BaseActivity() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(NoteDatabase.getInstance(this).taskItemDao())
    }

    private val subTaskViewModel: SubTaskViewModel by viewModels {
        SubTaskViewModelFactory(NoteDatabase.getInstance(this).subTaskDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val state = intent.getBooleanExtra(Constants.STATE_TASK_PRIORITY_ACTIVITY, false)
        val isCompleteTask =
            intent.getBooleanExtra(Constants.STATE_TASK_IS_COMPLETE_ACTIVITY, false)
        val category = intent.getStringExtra("Category") ?: ""
        Log.d(Constants.APP_LOG, category)

        setComposeContent {
            ShowAllTasksActivityScreen(
                taskViewModel,
                category,
                state,
                subTaskViewModel,
                isCompleteTask
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun ShowAllTasksActivityScreen(
    taskViewModel: TaskViewModel,
    category: String,
    priority: Boolean,
    subTaskViewModel: SubTaskViewModel,
    isCompleteTask: Boolean
) {
    val allTask by taskViewModel.taskElement.collectAsState(emptyList())
    val activity = LocalContext.current as? Activity
    val lazyState = rememberLazyListState()
    val context = LocalContext.current
    var titleActivity by remember { mutableStateOf(context.getString(R.string.task_bottom_bar)) }

    val filteredTask = if (!isCompleteTask) {
        if (priority) {
            allTask.filter { it.isComplete == false }.filter { it.priorityFlag == 2 }
        } else {
            if (category.isEmpty()) {
                allTask
            } else {
                allTask.filter { it.category == category }
            }
        }
    } else {
        allTask.filter { it.isComplete == isCompleteTask }
    }

    titleActivity = if (!isCompleteTask) {
        if (priority) {
            stringResource(R.string.priority_high)
        } else {
            category.ifEmpty {
                stringResource(R.string.task_bottom_bar)
            }
        }
    } else {
        stringResource(R.string.task_completed)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = titleActivity,
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
        }
    ) { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingValues.calculateTopPadding() + 12.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = paddingValues.calculateBottomPadding()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            BannerAds()

            Spacer(Modifier.height(12.dp))

            if (filteredTask.isNotEmpty()) {
                LazyColumn(
                    state = lazyState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(filteredTask) { index,tasks ->
                        TaskLazyItems(
                            index = index,
                            task = tasks,
                            subTaskViewModel = subTaskViewModel,
                            viewModel = taskViewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        )
                    }
                }
            }
        }
    }
}
