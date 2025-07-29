package com.rarestardev.notemaster.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rarestardev.notemaster.R
import com.rarestardev.notemaster.activities.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun LanguageSelector() {
    val systemLang = Locale.getDefault().language
    val langList = listOf(stringResource(R.string.english), stringResource(R.string.persian))
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable {
                    saveLanguageChanged(context, "en")
                }
                .background(
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    MaterialTheme.shapes.small
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = langList[0],
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(
                    start = 10.dp, end = 10.dp
                )
            )

            if (systemLang == "en") {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(R.string.english),
                    tint = Color.Green,
                    modifier = Modifier.padding(
                        start = 10.dp, end = 10.dp
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable {
                    saveLanguageChanged(context, "fa")
                }
                .background(
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    MaterialTheme.shapes.small
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = langList[1],
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(
                    start = 10.dp, end = 10.dp
                )
            )

            if (systemLang == "fa") {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(R.string.persian),
                    tint = Color.Green,
                    modifier = Modifier.padding(
                        start = 10.dp, end = 10.dp
                    )
                )
            }
        }

        Text(
            text = stringResource(R.string.warning_after_changing_the_language),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
            ,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

private fun saveLanguageChanged(context: Context, lang: String) {
    CoroutineScope(Dispatchers.IO).launch {
        SettingsPreferences.saveLanguage(context, lang)
        withContext(Dispatchers.Main) {
            restartApp(context as Activity)
        }
    }
}

private fun restartApp(activity: Activity){
    val intent = Intent(activity, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
    activity.startActivity(intent)
    activity.finish()
    Runtime.getRuntime().exit(0)
}