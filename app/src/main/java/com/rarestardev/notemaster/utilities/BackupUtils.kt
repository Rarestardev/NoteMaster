package com.rarestardev.notemaster.utilities

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream

object BackupUtils {

    fun performBackup(context: Context, destinationUri: Uri, onComplete: (Boolean) -> Unit) {
        try {
            val dbFile = context.getDatabasePath(Constants.DATABASE_NAME)
            val inputStream = FileInputStream(dbFile)
            val outputStream = context.contentResolver.openOutputStream(destinationUri)

            inputStream.copyTo(outputStream!!)
            inputStream.close()
            outputStream.close()
            Log.d(Constants.APP_LOG,"Backup success!")
            onComplete(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(Constants.APP_LOG, "Backup : " + e.message.toString())
            onComplete(false)
        }
    }

    fun restoreBackup(context: Context, sourceUri: Uri, onComplete: (Boolean) -> Unit) {
        try {
            val dbFile = context.getDatabasePath(Constants.DATABASE_NAME)
            val inputStream = context.contentResolver.openInputStream(sourceUri)
            val outputStream = FileOutputStream(dbFile)

            inputStream!!.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            Log.d(Constants.APP_LOG,"Restore success!")
            onComplete(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(Constants.APP_LOG, "Restore : " + e.message.toString())
            onComplete(false)
        }
    }
}