package com.example.bikeexpensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "maintenance_reminders")
data class MaintenanceReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bikeId: Int = 1,
    val title: String,
    val description: String = "",
    val reminderType: ReminderType = ReminderType.OIL_CHANGE,
    val dueDate: Date? = null,
    val dueOdometer: Int,
    val currentOdometer: Int = 0,
    val intervalKm: Int = 5000, // Default interval for oil change
    val isRecurring: Boolean = true,
    val isActive: Boolean = true,
    val lastNotifiedOdometer: Int = 0,
    val createdAt: Date = Date(),
    val lastUpdated: Date = Date()
)

enum class ReminderType {
    OIL_CHANGE,
    BRAKE_PADS,
    TIRES,
    CHAIN_SPROCKET,
    AIR_FILTER,
    SPARK_PLUG,
    BATTERY,
    CUSTOM
}