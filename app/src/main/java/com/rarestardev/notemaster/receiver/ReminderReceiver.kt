package com.rarestardev.notemaster.receiver

import com.rarestardev.notemaster.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rarestardev.notemaster.enums.ReminderType
import com.rarestardev.notemaster.utilities.Constants
import kotlin.random.Random

@Suppress("CAST_NEVER_SUCCEEDS")
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("message") ?: "Reminder"
        val type = intent?.getStringExtra("type") ?: ReminderType.NOTIFICATION.name

        if (type == ReminderType.NOTIFICATION.name) {
            val manager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "reminder_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(channelId, "Reminder", NotificationManager.IMPORTANCE_HIGH)
                manager.createNotificationChannels(channel as List<NotificationChannel?>)
            }

            val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Reminder")
                .setContentText(message)
                .setSmallIcon(R.drawable.icon_note)
                .build()

            manager.notify(Random.nextInt(), notification)
        } else {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone.play()
        }

        Log.d(Constants.APP_LOG, "Reminder Receiver : $type")
    }

}