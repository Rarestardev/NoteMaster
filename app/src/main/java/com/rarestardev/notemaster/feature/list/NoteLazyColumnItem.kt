package com.rarestardev.notemaster.feature.list

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.activities.CreateNoteActivity
import com.rarestardev.notemaster.activities.ShowAllNotesActivity
import com.rarestardev.notemaster.model.Note
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.utilities.Constants

@Preview
@Composable
private fun ItemPreview() {
    NoteMasterTheme {
        NoteLazyItemTitle()
    }
}

@Preview
@Composable
private fun NoteLazyItemPreview() {
    val note = Note(
        title = "Test",
        type = "Hello world",
        timestamp = "2022"
    )

    NoteMasterTheme {
        NoteLazyItem(note)
    }
}

@Composable
fun NoteLazyItemTitle() {
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(bottom = 4.dp)
    ) {
        Text(
            text = stringResource(R.string.note),
            modifier = Modifier.constrainAs(createRef()) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            },
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = stringResource(R.string.more),
            modifier = Modifier
                .constrainAs(createRef()) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                .clickable {
                    context.startActivity(Intent(context, ShowAllNotesActivity::class.java))
                },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_field_label_color)
        )
    }
}

@Composable
fun NoteLazyItem(note: Note) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(0.5.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable {
                val createNoteActivity = Intent(context, CreateNoteActivity::class.java)
                createNoteActivity.putExtra(Constants.STATE_NOTE_ACTIVITY, true)
                context.startActivity(createNoteActivity)
            }
    ) {
        Text(
            text = note.title,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            val (type, timestamp) = createRefs()

            Text(
                text = note.type,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp)
                    .constrainAs(type) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(timestamp.top)
                    },
                color = MaterialTheme.colorScheme.onPrimary,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )

            Text(
                text = note.timestamp,
                modifier = Modifier.constrainAs(timestamp) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                },
                color = colorResource(R.color.text_field_label_color),
                maxLines = 1,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}