package com.rarestardev.taskora.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.taskora.R
import com.rarestardev.taskora.components.BannerAds
import com.rarestardev.taskora.enums.CalenderType
import com.rarestardev.taskora.enums.ThemeMode
import com.rarestardev.taskora.factory.CalendarViewModelFactory
import com.rarestardev.taskora.feature.CustomText
import com.rarestardev.taskora.settings.SecondSettingsActivity
import com.rarestardev.taskora.ui.theme.TaskoraTheme
import com.rarestardev.taskora.utilities.Constants
import com.rarestardev.taskora.view_model.CalenderViewModel

class SettingsActivity : BaseActivity() {

    private val calenderViewModel: CalenderViewModel by viewModels {
        CalendarViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setComposeContent {
            SettingsScreen(calenderViewModel)
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
private fun SettingsPreview() {
    val context = LocalContext.current
    TaskoraTheme(ThemeMode.SYSTEM) {
        SettingsScreen(CalenderViewModel(context))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun SettingsScreen(
    calenderViewModel: CalenderViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_bottom_bar),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.background
                ),
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
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 8.dp
                ),
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) { BannerAds() }
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(
                    top = innerPadding.calculateTopPadding() + 12.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = innerPadding.calculateBottomPadding() + 4.dp
                )
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryTitle("Tools")

            CalendarSwitch(calenderViewModel)

            CategoryTitle(stringResource(R.string.personalization))

            PersonalizationOption(title = stringResource(R.string.theme))

            PersonalizationOption(title = stringResource(R.string.language))

            Spacer(Modifier.height(16.dp))

            CategoryTitle(stringResource(R.string.backupRestore))

            PersonalizationOption(title = stringResource(R.string.backupRestore))

            Spacer(Modifier.height(16.dp))

            CategoryTitle(stringResource(R.string.other))

            AppVersionShow()

            PersonalizationOption(title = stringResource(R.string.about_app))

            PersonalizationOption(title = stringResource(R.string.follow_us))
        }

    }
}

@Composable
private fun AppVersionShow() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.shapes.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.version),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp
        )

        val version = try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            e.toString()
        }

        CustomText(
            text = "( $version )",
            modifier = Modifier.padding(end = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun CategoryTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSecondary,
        fontSize = 14.sp,
        maxLines = 1
    )
}

@Composable
private fun PersonalizationOption(title: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clickable {
                val intent = Intent(Intent(context, SecondSettingsActivity::class.java)).apply {
                    putExtra(Constants.STATE_SECOND_SETTINGS_ACTIVITY, title)
                }
                context.startActivity(intent)
            }
            .background(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.shapes.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp
        )

        Icon(
            painter = painterResource(R.drawable.outline_arrow_forward_ios_24),
            contentDescription = null,
            modifier = Modifier
                .size(22.dp)
                .padding(end = 8.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun CalendarSwitch(viewModel: CalenderViewModel) {
    val calenderType by viewModel.calenderType.collectAsState()
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.shapes.small)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.persian_calendar),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp
        )

        Switch(
            checked = calenderType == CalenderType.PERSIAN,
            onCheckedChange = { viewModel.toggleType(context) },
            colors = SwitchDefaults.colors().copy(
                checkedThumbColor = colorResource(R.color.priority_low),
                uncheckedThumbColor = colorResource(R.color.priority_low),
                checkedTrackColor = MaterialTheme.colorScheme.onSecondary
            )
        )
    }

    Spacer(Modifier.height(16.dp))
}