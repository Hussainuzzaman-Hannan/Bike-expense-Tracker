package com.example.bikeexpensetracker.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bikeexpensetracker.data.BikeExpenseDatabase
import com.example.bikeexpensetracker.utils.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val database = BikeExpenseDatabase.getDatabase(context)
    private val notificationHelper = NotificationHelper(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            checkAndSendReminders()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun checkAndSendReminders() {
        try {
            // Get current odometer from latest fuel entry
            val latestFuelEntry = database.fuelEntryDao().getLastFuelEntry().firstOrNull()
            val currentOdometer = latestFuelEntry?.odometer ?: return

            // Get active reminders
            val reminders = database.maintenanceReminderDao().getActiveReminders().firstOrNull() ?: return

            val dueReminders = mutableListOf<Pair<String, String>>()

            for (reminder in reminders) {
                // Check if due based on odometer
                val distanceSinceLastNotification = currentOdometer - reminder.lastNotifiedOdometer
                val isDue = currentOdometer >= reminder.dueOdometer &&
                        distanceSinceLastNotification >= reminder.intervalKm

                if (isDue && reminder.isActive) {
                    val message = "Service due at ${reminder.dueOdometer} km. Current: $currentOdometer km"
                    dueReminders.add(Pair(reminder.title, message))

                    // Update last notified odometer
                    database.maintenanceReminderDao().updateLastNotifiedOdometer(reminder.id, currentOdometer)

                    // If recurring, update next due date
                    if (reminder.isRecurring) {
                        val newDueOdometer = currentOdometer + reminder.intervalKm
                        val updatedReminder = reminder.copy(
                            dueOdometer = newDueOdometer,
                            lastUpdated = java.util.Date(),
                            currentOdometer = currentOdometer
                        )
                        database.maintenanceReminderDao().updateReminder(updatedReminder)
                    }
                }
            }

            // Send notifications
            if (dueReminders.isNotEmpty()) {
                notificationHelper.showMultipleReminders(dueReminders)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}