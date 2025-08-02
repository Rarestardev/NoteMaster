package com.rarestardev.taskora.activities

import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.rarestardev.taskora.database.NoteDatabase
import com.rarestardev.taskora.enums.ThemeMode
import com.rarestardev.taskora.factory.TaskViewModelFactory
import com.rarestardev.taskora.settings.SettingsPreferences
import com.rarestardev.taskora.ui.theme.NoteMasterTheme
import com.rarestardev.taskora.view_model.TaskViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale
import kotlin.getValue

abstract class BaseActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(NoteDatabase.getInstance(this).taskItemDao())
    }

    override fun attachBaseContext(newBase: Context) {
        runBlocking {
            val lang = SettingsPreferences.languageFlow(newBase).first()
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration(newBase.resources.configuration)
            config.setLocale(locale)
            val updatedContext = newBase.createConfigurationContext(config)
            super.attachBaseContext(updatedContext)
        }
    }

    fun setComposeContent(content: @Composable () -> Unit) {
        setContent {
            val context = LocalContext.current
            val themeFlow = remember { SettingsPreferences.getTheme(context) }
            val themeMode by produceState(
                initialValue = ThemeMode.SYSTEM,
                key1 = themeFlow
            ) {
                themeFlow.collect { value = it }
            }

            val language by SettingsPreferences.languageFlow(context).collectAsState("en")

            CompositionLocalProvider(
                LocalLayoutDirection provides if (language == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                NoteMasterTheme(
                    themeMode = themeMode,
                ) {
                    content()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        taskViewModel.scheduleNextReminderFromDb(this)
    }

    override fun onResume() {
        super.onResume()
        taskViewModel.scheduleNextReminderFromDb(this)
    }
}