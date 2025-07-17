package com.rarestardev.notemaster.components.pager_view

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rarestardev.notemaster.activities.ShowAllNotesActivity
import com.rarestardev.notemaster.view_model.NoteEditorViewModel

@Composable
fun NoteViewPager(noteEditorViewModel: NoteEditorViewModel) {
    val noteList by noteEditorViewModel.allNote.collectAsState(initial = emptyList())

    val filterNoteList = noteList
        .filter { it.priority == 2 }
        .sortedByDescending { it.priority }
        .take(6)

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                context.startActivity(Intent(context, ShowAllNotesActivity::class.java))
            },
        state = rememberLazyListState()
    ) {
        items(filterNoteList) { note ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
        }
    }
}