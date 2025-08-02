package com.rarestardev.taskora.activities

import android.annotation.SuppressLint
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.database.NoteDatabase
import com.rarestardev.taskora.enums.ReminderType
import com.rarestardev.taskora.enums.ThemeMode
import com.rarestardev.taskora.factory.TaskViewModelFactory
import com.rarestardev.taskora.ui.theme.NoteMasterTheme
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.view_model.TaskViewModel

class AlarmViewActivity : BaseActivity() {

    private var ringtone: Ringtone? = null

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(NoteDatabase.getInstance(this).taskItemDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
        try {
            ringtone?.play()
        } catch (e: Exception) {
            Log.e(Constants.APP_LOG, "Error playing alarm sound", e)
        }

        val title = intent.getStringExtra(Constants.ALARM_TITLE) ?: ""
        val msg = intent.getStringExtra(Constants.ALARM_MESSAGE) ?: ""
        val id = intent.getIntExtra(Constants.ALARM_ID, 0)

        setComposeContent {
            AlarmScreen(title, msg) {
                stopRingtone()
                if (id != 0){
                    taskViewModel.updateReminder(ReminderType.NONE.name,id)
                }
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRingtone()
    }

    private fun stopRingtone() {
        ringtone?.let {
            if (it.isPlaying) {
                it.stop()
            }
        }
    }
}


@Preview
@Composable
private fun AlarmScreenPreview() {
    NoteMasterTheme(ThemeMode.DARK) {
        AlarmScreen(
            "Hi",
            "Test"
        ) {}
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun AlarmScreen(title: String, description: String, onDismissAlarm: () -> Unit) {

    BackHandler {
        onDismissAlarm()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 8.dp
                )
            ) {
                Button(
                    onClick = onDismissAlarm,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                        tint = Color.White
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = stringResource(R.string.stop),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(
                    top = paddingValues.calculateTopPadding() + 80.dp,
                    bottom = paddingValues.calculateBottomPadding() + 20.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    textAlign = TextAlign.Start,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(120.dp))

            RingingClockIcon()
        }
    }
}

@Composable
private fun RingingClockIcon() {
    val infiniteTransition = rememberInfiniteTransition()
    val offestX by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(delayMillis = 200, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(160.dp)
            .graphicsLayer {
                translationX = offestX
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.icons_alarm_clock),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(120.dp)
        )
    }
}