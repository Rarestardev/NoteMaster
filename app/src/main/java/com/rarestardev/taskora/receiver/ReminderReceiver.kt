package com.rarestardev.taskora.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rarestardev.taskora.R
import com.rarestardev.taskora.activities.MainActivity
import com.rarestardev.taskora.enums.ReminderType
import com.rarestardev.taskora.service.ReminderService
import com.rarestardev.taskora.utilities.Constants
import kotlin.random.Random

@Suppress("CAST_NEVER_SUCCEEDS")
class ReminderReceiver : BroadcastReceiver() {

    private var id: Int = 0
    private lateinit var message: String
    private lateinit var title: String
    private lateinit var type: String

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action == Constants.CANCEL_ALARM) {
                val serviceIntent = Intent(context, ReminderService::class.java).apply {
                    setAction(Constants.CANCEL_ALARM)
                }
                context?.stopService(serviceIntent)

                Log.d(Constants.APP_LOG, "stop foreground service")
            }

            id = it.getIntExtra(Constants.ALARM_ID, 0)
            message = it.getStringExtra(Constants.ALARM_MESSAGE) ?: "ReminderMessage"
            title = it.getStringExtra(Constants.ALARM_TITLE) ?: "ReminderTitle"
            type = it.getStringExtra(Constants.ALARM_TYPE) ?: ""
        }

        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Taskora:WakeLock")
        wakeLock.acquire(10000L)

        when (type) {
            ReminderType.NOTIFICATION.name -> {
                showNotification(context)
            }

            ReminderType.ALARM.name -> {
                startAlarmService(context)
            }
            else -> {
                Log.w(Constants.APP_LOG, "no reminder receiver is type empty")
            }
        }

        Log.d(Constants.APP_LOG, "Reminder Receiver Triggered : $type")

        wakeLock.release()
    }

    private fun showNotification(context: Context) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel"

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
    }

    private fun startAlarmService(context: Context){
        val serviceIntent = Intent(context, ReminderService::class.java).apply {
            putExtra(Constants.ALARM_MESSAGE, message)
            putExtra(Constants.ALARM_TITLE, title)
            putExtra(Constants.ALARM_ID, id)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        }
        context.startService(serviceIntent)
    }
}