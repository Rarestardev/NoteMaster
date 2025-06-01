package com.rarestardev.notemaster.designs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.notemaster.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowPopupMenuPriority() {
    var showPopupMenu by remember { mutableStateOf(false) }
    var flagColor by remember { mutableIntStateOf(R.color.priority_low) }
    val priorityColor = listOf(
        R.color.priority_high,
        R.color.priority_medium,
        R.color.priority_low
    )

    ExposedDropdownMenuBox(
        expanded = showPopupMenu,
        onExpandedChange = { showPopupMenu = !showPopupMenu }
    ) {
        IconButton(
            onClick = {
                showPopupMenu = true
            },
            modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable, true)
        ) {
            Icon(
                painterResource(R.drawable.icons_flag), stringResource(R.string.setflagonnote),
                tint = colorResource(flagColor)
            )
        }


        DropdownMenu(
            expanded = showPopupMenu,
            onDismissRequest = { showPopupMenu = false },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(0.5.dp, colorResource(R.color.second_color_2))
        ) {
            PopupItems(
                title = stringResource(R.string.priority_low),
                iconTint = priorityColor[2]
            ) {
                flagColor = priorityColor[2]
                showPopupMenu = false
            }

            PopupItems(
                title = stringResource(R.string.priority_medium),
                iconTint = priorityColor[1]
            ) {
                flagColor = priorityColor[1]
                showPopupMenu = false
            }

            PopupItems(
                title = stringResource(R.string.priority_high),
                iconTint = priorityColor[0]
            ) {
                flagColor = priorityColor[0]
                showPopupMenu = false
            }
        }
    }
}