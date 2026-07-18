package com.example.bikeexpensetracker.data.dao

import androidx.room.*
import com.example.bikeexpensetracker.model.MaintenanceEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceEntryDao {
    @Query("SELECT * FROM maintenance_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    fun getMaintenanceEntriesByBike(bikeId: Int): Flow<List<MaintenanceEntry>>

    @Insert
    suspend fun insertMaintenanceEntry(entry: MaintenanceEntry): Long

    @Delete
    suspend fun deleteMaintenanceEntry(entry: MaintenanceEntry)

    @Query("SELECT SUM(cost) FROM maintenance_entries WHERE bikeId = :bikeId")
    fun getTotalMaintenanceCost(bikeId: Int): Flow<Double>
}