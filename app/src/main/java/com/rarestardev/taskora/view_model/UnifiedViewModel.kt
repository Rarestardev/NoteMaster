package com.rarestardev.taskora.view_model

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rarestardev.taskora.R
import com.rarestardev.taskora.dao.NoteDao
import com.rarestardev.taskora.dao.SubTaskDao
import com.rarestardev.taskora.dao.TaskItemDao
import com.rarestardev.taskora.model.Note
import com.rarestardev.taskora.model.SubTask
import com.rarestardev.taskora.model.Task
import com.rarestardev.taskora.utilities.AES
import com.rarestardev.taskora.utilities.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */

class UnifiedViewModel(
    private val noteDao: NoteDao,
    private val taskDao: TaskItemDao,
    private val subTaskDao: SubTaskDao
) : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    fun backupToUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val notes = noteDao.getAllNotes().first()
                val tasks = taskDao.getAll().first()
                val subtasks = subTaskDao.getAllSubTask().first()

                val dataMap = mapOf(
                    "notes" to notes,
                    "tasks" to tasks,
                    "subtasks" to subtasks
                )

                val json = Gson().toJson(dataMap)
                val encrypted = AES.encrypt(json)

                val outputStream = context.contentResolver.openOutputStream(uri)
                outputStream?.use {
                    it.write(encrypted.toByteArray(Charsets.UTF_8))
                    it.flush()
                } ?: throw IOException("❌ Output stream is null")

                withContext(Dispatchers.Main) {
                    Toast.makeText(context,
                        context.getString(R.string.saved_backup_message), Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context,
                        context.getString(R.string.error_saved_backup_message), Toast.LENGTH_SHORT).show()
                }
                Log.e(Constants.APP_LOG, "Backup error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun restoreFromUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context,
                            context.getString(R.string.not_find_backup_file), Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val encrypted = inputStream.readBytes().toString(Charsets.UTF_8)
                val decrypted = AES.decrypt(encrypted)

                Log.d(Constants.APP_LOG, decrypted)

                val restoredMap: Map<String, JsonElement> = Gson().fromJson(
                    decrypted,
                    object : TypeToken<Map<String, JsonElement>>() {}.type
                )

                val notes = parseSafeList<Note>(restoredMap["notes"])
                val tasks = parseSafeList<Task>(restoredMap["tasks"])
                val subtasks = parseSafeList<SubTask>(restoredMap["subtasks"])

                val total = notes.size + tasks.size + subtasks.size
                var count = 0

                notes.forEach {
                    noteDao.insertNote(it)
                    count++
                    _progress.value = count.toFloat() / total
                    delay(20)
                }

                tasks.forEach {
                    taskDao.insert(it)
                    count++
                    _progress.value = count.toFloat() / total
                    delay(20)
                }

                subtasks.forEach {
                    subTaskDao.insertSubtask(it)
                    count++
                    _progress.value = count.toFloat() / total
                    delay(20)
                }

                Log.d(Constants.APP_LOG,"Restored...")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context,
                        context.getString(R.string.error_restore_file), Toast.LENGTH_SHORT).show()
                }
                Log.e(Constants.APP_LOG, "Restore error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    inline fun <reified T> parseSafeList(jsonElement: JsonElement?): List<T> {
        return when {
            jsonElement == null || jsonElement.isJsonNull -> emptyList()
            jsonElement.isJsonArray -> Gson().fromJson(jsonElement, object : TypeToken<List<T>>() {}.type)
            else -> emptyList()
        }
    }
}