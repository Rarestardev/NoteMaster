package com.rarestardev.notemaster.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rarestardev.notemaster.enums.CalenderType
import com.rarestardev.notemaster.view_model.CalenderViewModel

@Composable
private fun Test(viewModel: CalenderViewModel) {
    val calenderType by viewModel.calenderType.collectAsState()
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Calender Type :", Modifier.padding(end = 8.dp))

        Switch(
            checked = calenderType == CalenderType.PERSIAN,
            onCheckedChange = { viewModel.toggleType(context) }
        )

        Spacer(Modifier.width(8.dp))
        Text(text = if (calenderType == CalenderType.PERSIAN) "Persian" else "Gregorian")
    }

    Spacer(Modifier.height(16.dp))
}