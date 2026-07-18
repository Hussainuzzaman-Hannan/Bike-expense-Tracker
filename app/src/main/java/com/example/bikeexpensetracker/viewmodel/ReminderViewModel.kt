package com.example.bikeexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeexpensetracker.data.BikeExpenseDatabase
import com.example.bikeexpensetracker.model.MaintenanceReminder
import com.example.bikeexpensetracker.model.ReminderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val database = BikeExpenseDatabase.getDatabase(application)

    val reminders: Flow<List<MaintenanceReminder>> = database.maintenanceReminderDao().getActiveReminders()

    fun addReminder(title: String, type: ReminderType, intervalKm: Int) {
        viewModelScope.launch {
            try {
                // Get current odometer from latest fuel entry - Fixed here
                val latestFuelEntry = database.fuelEntryDao().getLastFuelEntry().firstOrNull()
                val currentOdometer = latestFuelEntry?.odometer ?: 0

                val reminder = MaintenanceReminder(
                    title = title,
                    reminderType = type,
                    dueOdometer = currentOdometer + intervalKm,
                    intervalKm = intervalKm,
                    currentOdometer = currentOdometer,
                    isActive = true
                )
                database.maintenanceReminderDao().insertReminder(reminder)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleReminderActive(reminder: MaintenanceReminder) {
        viewModelScope.launch {
            try {
                val updatedReminder = reminder.copy(isActive = !reminder.isActive)
                database.maintenanceReminderDao().updateReminder(updatedReminder)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteReminder(reminder: MaintenanceReminder) {
        viewModelScope.launch {
            try {
                database.maintenanceReminderDao().deleteReminder(reminder)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateOdometerForReminders(odometer: Int) {
        viewModelScope.launch {
            try {
                val reminders = database.maintenanceReminderDao().getActiveReminders().firstOrNull() ?: return@launch
                reminders.forEach { reminder ->
                    database.maintenanceReminderDao().updateCurrentOdometer(reminder.id, odometer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}