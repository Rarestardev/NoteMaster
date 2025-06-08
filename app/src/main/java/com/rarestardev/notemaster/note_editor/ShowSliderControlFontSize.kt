package com.rarestardev.notemaster.note_editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.view_model.NoteEditorViewModel

@Preview
@Composable
private fun SliderPreview() {
    NoteMasterTheme {
        val noteEditorViewModel = NoteEditorViewModel()
        CustomSliderFontSize(viewModel = noteEditorViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSliderFontSize(viewModel: NoteEditorViewModel) {
    var isShowSliderController by remember { mutableStateOf(false) }
    val defaultSliderColor = MaterialTheme.colorScheme.onSecondary

    ExposedDropdownMenuBox(
        expanded = isShowSliderController,
        onExpandedChange = { isShowSliderController = !isShowSliderController }
    ) {
        IconButton(
            onClick = { isShowSliderController = true },
            modifier = Modifier
                .padding(6.dp)
                .menuAnchor(MenuAnchorType.SecondaryEditable, true)
        ) {
            Image(
                painter = painterResource(R.drawable.icons_text_height),
                contentDescription = stringResource(R.string.text_size_icon_desc),
                modifier = Modifier.size(dimensionResource(R.dimen.bottom_bar_note_Activity_icon_size))
            )
        }

        DropdownMenu(
            expanded = isShowSliderController,
            onDismissRequest = { isShowSliderController = false },
            modifier = Modifier
                .wrapContentSize(align = Alignment.Center)
                .border(
                    0.3.dp,
                    MaterialTheme.colorScheme.onSecondary,
                    RoundedCornerShape(8.dp)
                )
                .background(
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    RoundedCornerShape(8.dp)
                )
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
        ) {
            Text(
                text = "${viewModel.textSizeSliderValue}",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = viewModel.textSizeSliderValue.value,
                onValueChange = { newSize -> viewModel.updateTextSize(newSize) },
                valueRange = 11f..36f,
                modifier = Modifier
                    .width(240.dp)
                    .height(40.dp)
                    .padding(6.dp),
                colors = SliderDefaults.colors().copy(
                    thumbColor = defaultSliderColor,
                    inactiveTrackColor = MaterialTheme.colorScheme.background,
                    activeTrackColor = defaultSliderColor
                )
            )
        }
    }
}