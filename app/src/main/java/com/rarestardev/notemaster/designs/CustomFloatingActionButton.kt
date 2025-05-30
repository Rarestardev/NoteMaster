package com.rarestardev.notemaster.designs

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.activities.CreateCapitalActivity
import com.rarestardev.notemaster.activities.CreateNoteActivity
import com.rarestardev.notemaster.activities.CreateTaskActivity
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme

@Preview
@Composable
private fun FloatActionButtonPreview() {
    NoteMasterTheme {
        FloatingActionMenu()
    }
}

private enum class MenuItemFab(val title: String) {
    ITEM1("Create note"),
    ITEM2("Capital"),
    ITEM3("Task")
}

@Composable
fun FloatingActionMenu() {
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    BackHandler {
        if (expanded) expanded = false
    }

    Box(contentAlignment = Alignment.BottomEnd) {

        ConstraintLayout {
            val (fabRef, expandMenuRef) = createRefs()

            FloatingActionButton(
                onClick = { expanded = !expanded },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(fabRef) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Expand Menu", tint = Color.White)
            }// fab

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .padding(8.dp)
                    .animateContentSize()
                    .constrainAs(expandMenuRef) {
                        bottom.linkTo(fabRef.top)
                        end.linkTo(fabRef.end)
                    }
            ) {

                if (expanded) {
                    MenuItemFab.entries.forEach { item ->

                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.secondary)
                                .clickable {
                                    when (item.title) {
                                        MenuItemFab.ITEM1.title -> {
                                            expanded = false
                                            context.startActivity(
                                                Intent(
                                                    context,
                                                    CreateNoteActivity::class.java
                                                )
                                            )
                                        }

                                        MenuItemFab.ITEM2.title -> {
                                            expanded = false
                                            context.startActivity(
                                                Intent(
                                                    context,
                                                    CreateCapitalActivity::class.java
                                                )
                                            )
                                        }

                                        MenuItemFab.ITEM3.title -> {
                                            expanded = false
                                            context.startActivity(
                                                Intent(
                                                    context,
                                                    CreateTaskActivity::class.java
                                                )
                                            )
                                        }
                                    }
                                }
                        ) {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = item.title,
                                color = colorResource(R.color.drawer_text_icon_color),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                minLines = 1,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}
