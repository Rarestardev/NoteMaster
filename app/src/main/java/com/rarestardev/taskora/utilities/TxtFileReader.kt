package com.rarestardev.taskora.utilities

import android.content.Context
import java.io.IOException

object TxtFileReader {

    fun readTxtFromAssets(context: Context, fileName: String): String {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.message.toString()
        }
    }
}