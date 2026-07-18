package com.example.bikeexpensetracker.data.dao

import androidx.room.*
import com.example.bikeexpensetracker.model.MaintenanceReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceReminderDao {

    @Insert
    suspend fun insertReminder(reminder: MaintenanceReminder): Long

    @Update
    suspend fun updateReminder(reminder: MaintenanceReminder)

    @Delete
    suspend fun deleteReminder(reminder: MaintenanceReminder)

    @Query("SELECT * FROM maintenance_reminders WHERE isActive = 1 ORDER BY dueOdometer ASC")
    fun getActiveReminders(): Flow<List<MaintenanceReminder>>

    @Query("SELECT * FROM maintenance_reminders WHERE bikeId = :bikeId AND isActive = 1 ORDER BY dueOdometer ASC")
    fun getActiveRemindersByBike(bikeId: Int): Flow<List<MaintenanceReminder>>

    @Query("SELECT * FROM maintenance_reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: Int): MaintenanceReminder?

    @Query("UPDATE maintenance_reminders SET lastNotifiedOdometer = :odometer WHERE id = :reminderId")
    suspend fun updateLastNotifiedOdometer(reminderId: Int, odometer: Int)

    @Query("UPDATE maintenance_reminders SET currentOdometer = :odometer WHERE id = :reminderId")
    suspend fun updateCurrentOdometer(reminderId: Int, odometer: Int)
}