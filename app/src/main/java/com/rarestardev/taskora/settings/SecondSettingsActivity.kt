package com.rarestardev.taskora.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.rarestardev.taskora.R
import com.rarestardev.taskora.activities.BaseActivity
import com.rarestardev.taskora.database.NoteDatabase
import com.rarestardev.taskora.enums.ThemeMode
import com.rarestardev.taskora.factory.UnifiedVMFactory
import com.rarestardev.taskora.ui.theme.NoteMasterTheme
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.view_model.UnifiedViewModel
import kotlinx.coroutines.launch

class SecondSettingsActivity : BaseActivity() {

    val db = NoteDatabase.getInstance(this)

    private val unifiedViewModel: UnifiedViewModel by viewModels {
        UnifiedVMFactory(db.noteDao(),db.taskItemDao(),db.subTaskDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setComposeContent {
            val themeMode by SettingsPreferences.getTheme(this).collectAsState(ThemeMode.SYSTEM)
            val titleActivity =
                intent.getStringExtra(Constants.STATE_SECOND_SETTINGS_ACTIVITY) ?: ""

            Scaffold(
                topBar = { SettingsTopAppBar(titleActivity) },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when(titleActivity){
                        stringResource(R.string.theme) -> {
                            ThemeView(themeMode) {
                                lifecycleScope.launch {
                                    SettingsPreferences.saveTheme(this@SecondSettingsActivity, it)
                                }
                            }
                        }
                        stringResource(R.string.language) -> {
                            LanguageSelector()
                        }

                        stringResource(R.string.about_app) -> {

                        }

                        stringResource(R.string.backupRestore) -> {
                            BackupScreen(unifiedViewModel)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(title: String) {
    val activity = LocalContext.current as? Activity

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { activity?.finish() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_activity_desc),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
private fun SettingsPreview() {
    NoteMasterTheme(ThemeMode.SYSTEM) {
        ThemeView(ThemeMode.SYSTEM) {}
    }
}