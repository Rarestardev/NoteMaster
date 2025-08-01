package com.rarestardev.taskora.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.view_model.UnifiedViewModel

@Composable
fun BackupScreen(viewModel: UnifiedViewModel) {
    val context = LocalContext.current
    val progress by viewModel.progress.collectAsState()
    var address by remember { mutableStateOf("") }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )


    // ActivityResultContracts.CreateDocument()
    val launcherBackup =
        rememberLauncherForActivityResult(CreateDocument("todo/todo")) { uri ->
            if (uri != null) {
                viewModel.backupToUri(context, uri)
                address = uri.toString()
            }
        }

    val launcherRestore =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                viewModel.restoreFromUri(context, uri)
                address = uri.toString()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.wrong_restore_file), Toast.LENGTH_SHORT
                ).show()
            }
        }

    Column(
        Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        Icon(
            painter = painterResource(R.drawable.icon_backup),
            contentDescription = stringResource(R.string.backupRestore),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (address.isNotEmpty()) {
            Box(
                Modifier
                    .padding(12.dp)
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        MaterialTheme.shapes.small
                    )
            ) {
                Text(
                    text = address,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
                )
            }
        }

        LinearProgressIndicator(
            progress = { animatedProgress },
            trackColor = MaterialTheme.colorScheme.onSecondaryContainer,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Text(
            text = "${(progress * 100).toInt()} %",
            color = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    MaterialTheme.shapes.small
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { launcherBackup.launch("note_master_backup.json") },
                modifier = Modifier.padding(12.dp)
                    .width(140.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = stringResource(R.string.choose_backup_file),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = { launcherRestore.launch(arrayOf("application/json")) },
                modifier = Modifier.padding(12.dp).width(140.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = stringResource(R.string.choose_restore_file),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}