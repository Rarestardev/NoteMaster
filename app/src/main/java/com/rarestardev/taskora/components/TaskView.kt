package com.rarestardev.taskora.components

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.activities.ShowAllTasksActivity
import com.rarestardev.taskora.enums.ReminderType
import com.rarestardev.taskora.list_item.TaskLazyItems
import com.rarestardev.taskora.model.Task
import com.rarestardev.taskora.ui.theme.TaskoraTheme
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.utilities.previewFakeTaskViewModel
import com.rarestardev.taskora.utilities.previewSubTaskViewModel
import com.rarestardev.taskora.view_model.SubTaskViewModel
import com.rarestardev.taskora.view_model.TaskViewModel

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */
@Preview
@Composable
private fun TaskItemPreview() {
    TaskoraTheme {
        TaskView(previewFakeTaskViewModel(), previewSubTaskViewModel())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskView(viewModel: TaskViewModel, subTaskViewModel: SubTaskViewModel) {
    val allTasks by viewModel.taskElement.collectAsState(emptyList())
    val lazyState = rememberLazyListState()
    val taskElement = allTasks.filter { it.isComplete == false }

    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleList(stringResource(R.string.task_bottom_bar), taskElement, false)

        if (taskElement.isNotEmpty()) {
            LazyRow(
                state = lazyState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(4.dp)
            ) {
                if (taskElement.isNotEmpty()) {
                    itemsIndexed(taskElement.take(10)) { index, task ->
                        TaskLazyItems(
                            index,
                            task,
                            subTaskViewModel,
                            viewModel,
                            modifier = Modifier
                                .width(240.dp)
                                .height(140.dp)
                        )

                        task.reminderTime?.let {
                            if (it != 0L && it < System.currentTimeMillis()) {
                                viewModel.updateReminder(ReminderType.NONE.name, task.id)
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = stringResource(R.string.task_list_is_empty),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(top = 50.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompleteTaskView(viewModel: TaskViewModel, subTaskViewModel: SubTaskViewModel) {
    val allTasks by viewModel.taskElement.collectAsState(emptyList())
    val lazyState = rememberLazyListState()
    val taskElement = allTasks.filter { it.isComplete == true }

    Column(
        Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleList(stringResource(R.string.task_completed), taskElement, true)

        if (taskElement.isNotEmpty()) {
            LazyRow(
                state = lazyState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(4.dp)
            ) {
                if (taskElement.isNotEmpty()) {
                    itemsIndexed(taskElement.take(10)) { index, task ->
                        TaskLazyItems(
                            index,
                            task,
                            subTaskViewModel,
                            viewModel,
                            modifier = Modifier
                                .width(240.dp)
                                .height(140.dp)
                        )
                    }
                }
            }
        } else {
            Text(
                text = stringResource(R.string.task_list_is_empty),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TitleList(title: String, taskElement: List<Task>, isComplete: Boolean) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 12.dp)
        )

        if (taskElement.isNotEmpty()) {
            Text(
                text = stringResource(R.string.see_more),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.text_field_label_color),
                modifier = Modifier
                    .clickable {
                        val intent = Intent(context, ShowAllTasksActivity::class.java).apply {
                            putExtra(Constants.STATE_TASK_PRIORITY_ACTIVITY, false)
                            putExtra(Constants.STATE_TASK_IS_COMPLETE_ACTIVITY, isComplete)
                        }
                        context.startActivity(intent)
                    }
                    .padding(end = 12.dp)
            )
        }
    }

    Spacer(Modifier.height(6.dp))
}

