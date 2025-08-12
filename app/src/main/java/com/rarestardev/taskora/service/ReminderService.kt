package com.rarestardev.taskora.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rarestardev.taskora.R
import com.rarestardev.taskora.activities.MainActivity
import com.rarestardev.taskora.receiver.ReminderReceiver
import com.rarestardev.taskora.utilities.Constants

/**
 * ┌────────────────────────────────────────────┐
 * │ Developer: RareStar.dev                    │
 * │ App: Taskora | Productivity Redefined      │
 * │ GitHub: github.com/Rarestardev             │
 * └────────────────────────────────────────────┘
 */

class ReminderService : Service() {

    private lateinit var ringtone: Ringtone

    private lateinit var title: String
    private lateinit var message: String

    companion object {
        private const val CHANNEL_ID = "reminder_channel"
        private const val NOTIFICATION_ID = 234
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            title = it.getStringExtra(Constants.ALARM_TITLE) ?: ""
            message = it.getStringExtra(Constants.ALARM_MESSAGE) ?: ""
        }

        showNotification(title, message)

        Log.d(Constants.APP_LOG, "Running reminder service ...")
        return START_NOT_STICKY
    }

    @SuppressLint("ForegroundServiceType")
    private fun showNotification(title: String, message: String) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(notificationPendingIntent())
            .setSmallIcon(R.drawable.icons_alarm_clock)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setSound(null)
            .setOngoing(true)
            .addAction(
                0,
                getString(R.string.cancel),
                notificationAction()
            )
            .build()

        startForeground(NOTIFICATION_ID, notification)

        startAlarmSound()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }


    private fun notificationAction(): PendingIntent {
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            setAction(Constants.CANCEL_ALARM)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            5466,
            intent,
            flags
        )

        return pendingIntent
    }

    private fun notificationPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            1325,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return pendingIntent
    }

    private fun startAlarmSound() {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
        ringtone.play()
    }

    private fun stop() {
        if (::ringtone.isInitialized && ringtone.isPlaying) {
            val bi = Intent(this, ReminderReceiver::class.java).apply {
                setAction(Constants.CANCEL_ALARM)
            }
            sendBroadcast(bi)
            ringtone.stop()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        stop()
        super.onDestroy()
    }
}