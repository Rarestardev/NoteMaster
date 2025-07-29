package com.rarestardev.notemaster.activities

import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.rarestardev.notemaster.enums.ThemeMode
import com.rarestardev.notemaster.settings.SettingsPreferences
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

abstract class BaseActivity : ComponentActivity() {

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
            val themeMode by produceState<ThemeMode>(
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
}