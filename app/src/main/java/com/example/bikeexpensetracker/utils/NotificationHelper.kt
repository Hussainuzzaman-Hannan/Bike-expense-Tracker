package com.example.bikeexpensetracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.bikeexpensetracker.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "maintenance_reminder_channel"
        const val CHANNEL_NAME = "Maintenance Reminders"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for maintenance reminders"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showMaintenanceReminder(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // You need to add this icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun showMultipleReminders(reminders: List<Pair<String, String>>) {
        if (reminders.isEmpty()) return

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Maintenance Reminders")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (reminders.size == 1) {
            notificationBuilder
                .setContentTitle(reminders[0].first)
                .setContentText(reminders[0].second)
        } else {
            val inboxStyle = NotificationCompat.InboxStyle()
            inboxStyle.setBigContentTitle("${reminders.size} Maintenance Reminders")
            reminders.forEach { reminder ->
                inboxStyle.addLine("${reminder.first}: ${reminder.second}")
            }
            notificationBuilder.setStyle(inboxStyle)
                .setContentTitle("${reminders.size} Maintenance Reminders")
                .setContentText("Tap to view details")
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}