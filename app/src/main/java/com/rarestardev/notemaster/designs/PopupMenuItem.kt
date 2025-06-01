package com.rarestardev.notemaster.designs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.notemaster.R

@Composable
fun PopupItems(title: String, iconTint: Int, onClickPopup: () -> Unit) {
    DropdownMenuItem(
        text = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(4.dp)
            )
        },
        onClick = onClickPopup,
        leadingIcon = {
            Icon(
                painterResource(R.drawable.icons_flag),
                stringResource(R.string.setflagonnote),
                tint = colorResource(iconTint)
            )
        }
    )
}