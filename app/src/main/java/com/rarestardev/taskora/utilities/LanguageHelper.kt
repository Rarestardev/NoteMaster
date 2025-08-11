package com.rarestardev.taskora.utilities

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.rarestardev.taskora.R
import java.util.Locale

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */

object LanguageHelper {

    @Composable
     fun getEnLanguageListCategory() : Array<String> {
        val context = LocalContext.current

        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale("en"))
        val localizedContext = context.createConfigurationContext(config)
        return localizedContext.resources.getStringArray(R.array.task_categories)
    }

    @Composable
     fun getFaLanguageListCategory() : Array<String> {
        val context = LocalContext.current

        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale("fa"))
        val localizedContext = context.createConfigurationContext(config)
        return localizedContext.resources.getStringArray(R.array.task_categories)
    }

    @Composable
    fun getEnLanguageListPriorityFlag(stringRes: Int) : String {
        val context = LocalContext.current

        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale("en"))
        val localizedContext = context.createConfigurationContext(config)
        return localizedContext.resources.getString(stringRes)
    }

    @Composable
    fun getFaLanguageListPriorityFlag(stringRes: Int) : String {
        val context = LocalContext.current

        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale("fa"))
        val localizedContext = context.createConfigurationContext(config)
        return localizedContext.resources.getString(stringRes)
    }
}