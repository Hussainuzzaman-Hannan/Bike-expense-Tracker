package com.example.bikeexpensetracker.utils

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class ReminderManager(private val context: Context) {

    companion object {
        private const val WORK_NAME = "maintenance_reminder_work"

        @Volatile
        private var instance: ReminderManager? = null

        fun getInstance(context: Context): ReminderManager {
            return instance ?: synchronized(this) {
                instance ?: ReminderManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun scheduleReminderCheck() {
        val workRequest = PeriodicWorkRequestBuilder<com.example.bikeexpensetracker.workers.ReminderWorker>(
            1, TimeUnit.HOURS // Check every hour
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
        ).setInitialDelay(1, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun cancelReminderCheck() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    fun scheduleOneTimeReminder(delayMinutes: Long) {
        val workRequest = OneTimeWorkRequestBuilder<com.example.bikeexpensetracker.workers.ReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}