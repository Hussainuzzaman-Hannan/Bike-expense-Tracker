package com.example.bikeexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeexpensetracker.data.BikeExpenseDatabase
import com.example.bikeexpensetracker.data.SelectedBikeManager
import com.example.bikeexpensetracker.model.MaintenanceReminder
import com.example.bikeexpensetracker.model.ReminderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val database = BikeExpenseDatabase.getDatabase(application)

    val reminders: Flow<List<MaintenanceReminder>> = SelectedBikeManager.selectedBikeId.flatMapLatest { bikeId ->
        database.maintenanceReminderDao().getActiveRemindersByBike(bikeId)
    }

    fun addReminder(title: String, type: ReminderType, intervalKm: Int) {
        viewModelScope.launch {
            try {
                val bikeId = SelectedBikeManager.getSelectedBikeId()
                // Get current odometer from latest fuel entry of the selected bike
                val latestFuelEntry = database.fuelEntryDao().getFuelEntriesByBike(bikeId).firstOrNull()?.firstOrNull()
                val currentOdometer = latestFuelEntry?.odometer ?: 0

                val reminder = MaintenanceReminder(
                    bikeId = bikeId,
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
                val bikeId = SelectedBikeManager.getSelectedBikeId()
                val reminders = database.maintenanceReminderDao().getActiveRemindersByBike(bikeId).firstOrNull() ?: return@launch
                reminders.forEach { reminder ->
                    database.maintenanceReminderDao().updateCurrentOdometer(reminder.id, odometer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}