package com.example.bikeexpensetracker.data.dao

import androidx.room.*
import com.example.bikeexpensetracker.model.MaintenanceEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MaintenanceDao {

    @Insert
    suspend fun insertMaintenanceEntry(entry: MaintenanceEntry): Long

    @Delete
    suspend fun deleteMaintenanceEntry(entry: MaintenanceEntry)

    @Update
    suspend fun updateMaintenanceEntry(entry: MaintenanceEntry)

    @Query("SELECT * FROM maintenance_entries ORDER BY date DESC")
    fun getAllMaintenanceEntries(): Flow<List<MaintenanceEntry>>

    @Query("SELECT * FROM maintenance_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    fun getMaintenanceEntriesByBike(bikeId: Int): Flow<List<MaintenanceEntry>>

    // Get total maintenance cost - returns 0 instead of null
    @Query("SELECT IFNULL(SUM(cost), 0) FROM maintenance_entries WHERE bikeId = :bikeId")
    fun getTotalMaintenanceCost(bikeId: Int): Flow<Double>

    @Query("SELECT * FROM maintenance_entries WHERE category = :category ORDER BY date DESC")
    fun getMaintenanceByCategory(category: String): Flow<List<MaintenanceEntry>>

    @Query("SELECT * FROM maintenance_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getMaintenanceByDateRange(startDate: Date, endDate: Date): Flow<List<MaintenanceEntry>>

    // Delete all maintenance entries for a bike
    @Query("DELETE FROM maintenance_entries WHERE bikeId = :bikeId")
    suspend fun deleteAllMaintenanceEntries(bikeId: Int)
}