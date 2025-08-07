package com.rarestardev.taskora.components.pager_view

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.activities.ShowAllNotesActivity
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.view_model.NoteEditorViewModel

@Composable
fun NoteViewPager(noteEditorViewModel: NoteEditorViewModel) {
    val noteList by noteEditorViewModel.allNote.collectAsState(initial = emptyList())

    val filterNoteList = noteList
        .filter { it.priority == 2 }
        .sortedByDescending { it.priority }
        .take(5)

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                val intent = Intent(context, ShowAllNotesActivity::class.java).apply {
                    putExtra(Constants.STATE_NOTE_PRIORITY_ACTIVITY, true)
                }
                context.startActivity(intent)
            },
        state = rememberLazyListState()
    ) {

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.high_priority_notes),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 0.3.dp,
                    color = Color.White
                )
            }
        }


        if (filterNoteList.isNotEmpty()) {
            items(filterNoteList) { note ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 4.dp,
                            bottom = 4.dp
                        )
                        .border(
                            0.3.dp,
                            Color.White,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(6.dp)
                ) {
                    Text(
                        text = note.noteTitle,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(2.dp))
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_item),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}