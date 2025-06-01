package com.rarestardev.notemaster.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.rarestardev.notemaster.database.AppDatabase
import com.rarestardev.notemaster.designs.NoteActivityDesign
import com.rarestardev.notemaster.ui.theme.NoteMasterTheme
import com.rarestardev.notemaster.utilities.Constants
import com.rarestardev.notemaster.view_model.NoteViewModel
import com.rarestardev.notemaster.view_model.NoteViewModelFactory

class CreateNoteActivity : ComponentActivity() {

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(AppDatabase.getInstance(this).noteDao())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteMasterTheme {
                val isPreview = intent.getBooleanExtra(Constants.STATE_NOTE_ACTIVITY, false)

                if (!isPreview) NoteActivityDesign() else Log.d(Constants.APP_LOG, "Preview")
            }
        }
    }
}


