package com.rarestardev.notemaster.note_editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.view_model.NoteEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDirectionTextDropMenu(viewModel: NoteEditorViewModel) {
    var isShowMenu by remember { mutableStateOf(false) }

    val textAlignList = listOf(
        TextAlign.Start,
        TextAlign.Center,
        TextAlign.End
    )


    ExposedDropdownMenuBox(
        expanded = isShowMenu,
        onExpandedChange = { isShowMenu = !isShowMenu }
    ) {
        IconButton(
            onClick = {
                isShowMenu = !isShowMenu
            },
            modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable, true)
        ) {
            Image(
                painterResource(R.drawable.icons_align_text_center),
                stringResource(R.string.align_center_text_filed_desc)
            )
        }

        DropdownMenu(
            expanded = isShowMenu,
            onDismissRequest = { isShowMenu = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.onSecondaryContainer),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                0.3.dp, MaterialTheme.colorScheme.onSecondary
            )
        ) {
            CustomDropMenuItem(
                stringResource(R.string.align_left_text_filed_desc),
                icon = R.drawable.icons_align_text_left
            ) {
                viewModel.setDirection(textAlignList[0])
            }

            CustomDropMenuItem(
                name = stringResource(R.string.align_center_text_filed_desc),
                icon = R.drawable.icons_align_text_center
            ) {
                viewModel.setDirection(textAlignList[1])
            }

            CustomDropMenuItem(
                name = stringResource(R.string.align_end_text_filed_desc),
                icon = R.drawable.icons_align_text_right
            ) {
                viewModel.setDirection(textAlignList[2])
                isShowMenu = false
            }
        }
    }
}

@Composable
private fun CustomDropMenuItem(name: String, icon: Int, onClickItem: () -> Unit) {
    DropdownMenuItem(
        text = { DropMenuText(name) },
        onClick = { onClickItem },
        leadingIcon = { LeadingIcon(icon, name) }
    )
}

@Composable
private fun DropMenuText(name: String) {
    Text(
        text = name,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
private fun LeadingIcon(icon: Int, contentDescription: String) {
    Image(
        painterResource(icon),
        contentDescription,
        modifier = Modifier.size(
            dimensionResource(R.dimen.bottom_bar_note_Activity_icon_size)
        )
    )
}