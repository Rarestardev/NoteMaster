package com.rarestardev.taskora.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rarestardev.taskora.R
import com.rarestardev.taskora.activities.AlarmViewActivity
import com.rarestardev.taskora.activities.MainActivity
import com.rarestardev.taskora.enums.ReminderType
import com.rarestardev.taskora.utilities.Constants
import kotlin.random.Random

@Suppress("CAST_NEVER_SUCCEEDS")
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getIntExtra(Constants.ALARM_ID,0)
        val message = intent?.getStringExtra(Constants.ALARM_MESSAGE) ?: "ReminderMessage"
        val title = intent?.getStringExtra(Constants.ALARM_TITLE) ?: "ReminderTitle"
        val type = intent?.getStringExtra(Constants.ALARM_TYPE) ?: ReminderType.NOTIFICATION.name

        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Reminder:WakeLock")
        wakeLock.acquire(3000L)

        if (type == ReminderType.NOTIFICATION.name) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "reminder_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Reminder",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for reminder notifications"
                    enableLights(true)
                    enableVibration(true)
                }
                manager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                323,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon_note)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            manager.notify(Random.nextInt(), notification)

        } else if (type == ReminderType.ALARM.name) {
            val intent = Intent(context, AlarmViewActivity::class.java).apply {
                putExtra(Constants.ALARM_TITLE,title)
                putExtra(Constants.ALARM_MESSAGE,message)
                putExtra(Constants.ALARM_ID,id)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }

            context.startActivity(intent)
        }

        Log.d(Constants.APP_LOG, "Reminder Receiver Triggered : $type")

        wakeLock.release()
    }
}