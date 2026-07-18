package com.example.bikeexpensetracker.data.dao

import androidx.room.*
import com.example.bikeexpensetracker.model.FuelEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface FuelEntryDao {

    // Insert a new fuel entry
    @Insert
    suspend fun insertFuelEntry(entry: FuelEntry): Long

    // Delete a fuel entry
    @Delete
    suspend fun deleteFuelEntry(entry: FuelEntry)

    // Update an existing fuel entry
    @Update
    suspend fun updateFuelEntry(entry: FuelEntry)

    // Get all fuel entries (newest first)
    @Query("SELECT * FROM fuel_entries ORDER BY date DESC")
    fun getAllFuelEntries(): Flow<List<FuelEntry>>

    // Get fuel entries for a specific bike
    @Query("SELECT * FROM fuel_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    fun getFuelEntriesByBike(bikeId: Int): Flow<List<FuelEntry>>

    // Get total fuel cost for a specific bike - returns 0 instead of null
    @Query("SELECT IFNULL(SUM(totalCost), 0) FROM fuel_entries WHERE bikeId = :bikeId")
    fun getTotalFuelCost(bikeId: Int): Flow<Double>

    // Get last fuel entry for mileage calculation
    @Query("SELECT * FROM fuel_entries ORDER BY date DESC LIMIT 1")
    fun getLastFuelEntry(): Flow<FuelEntry?>

    // Get total liters of fuel purchased - returns 0 instead of null
    @Query("SELECT IFNULL(SUM(liters), 0) FROM fuel_entries WHERE bikeId = :bikeId")
    fun getTotalLiters(bikeId: Int): Flow<Double>

    // Get fuel entries within a date range (for analytics)
    @Query("SELECT * FROM fuel_entries WHERE bikeId = :bikeId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getFuelEntriesByDateRange(
        bikeId: Int,
        startDate: Date,
        endDate: Date
    ): Flow<List<FuelEntry>>

    // Get fuel entries from last N days (for trend analysis)
    @Query("SELECT * FROM fuel_entries WHERE bikeId = :bikeId AND date >= :fromDate ORDER BY date ASC")
    fun getFuelEntriesFromDate(
        bikeId: Int,
        fromDate: Date
    ): Flow<List<FuelEntry>>

    // Get monthly fuel cost summary
    @Query("""
        SELECT 
            strftime('%Y', date/1000, 'unixepoch') as year,
            strftime('%m', date/1000, 'unixepoch') as month,
            SUM(totalCost) as totalCost,
            SUM(liters) as totalLiters
        FROM fuel_entries 
        WHERE bikeId = :bikeId
        GROUP BY year, month
        ORDER BY year DESC, month DESC
    """)
    fun getMonthlyFuelSummary(bikeId: Int): Flow<List<MonthlyFuelSummary>>

    // Get recent fuel entries for mileage calculation (with previous entry)
    @Query("SELECT * FROM fuel_entries WHERE bikeId = :bikeId ORDER BY date DESC LIMIT 10")
    fun getRecentFuelEntries(bikeId: Int): Flow<List<FuelEntry>>

    // Get total number of fuel entries - returns 0 instead of null
    @Query("SELECT IFNULL(COUNT(*), 0) FROM fuel_entries WHERE bikeId = :bikeId")
    fun getFuelEntryCount(bikeId: Int): Flow<Int>

    // Get average fuel price over time - returns 0 instead of null
    @Query("SELECT IFNULL(AVG(pricePerLiter), 0) FROM fuel_entries WHERE bikeId = :bikeId")
    fun getAverageFuelPrice(bikeId: Int): Flow<Double>

    // Get maximum odometer reading - returns 0 instead of null
    @Query("SELECT IFNULL(MAX(odometer), 0) FROM fuel_entries WHERE bikeId = :bikeId")
    fun getMaxOdometer(bikeId: Int): Flow<Int>

    // Get minimum odometer reading - returns 0 instead of null
    @Query("SELECT IFNULL(MIN(odometer), 0) FROM fuel_entries WHERE bikeId = :bikeId")
    fun getMinOdometer(bikeId: Int): Flow<Int>

    // Delete all fuel entries for a bike
    @Query("DELETE FROM fuel_entries WHERE bikeId = :bikeId")
    suspend fun deleteAllFuelEntries(bikeId: Int)

    // Get fuel efficiency trend (mileage calculations)
    @Query("""
        SELECT 
            f1.id,
            f1.date,
            f1.liters,
            f1.odometer,
            (f1.odometer - f2.odometer) as distance,
            ((f1.odometer - f2.odometer) / f1.liters) as mileage
        FROM fuel_entries f1
        LEFT JOIN fuel_entries f2 ON f2.id = (
            SELECT id FROM fuel_entries 
            WHERE date < f1.date AND bikeId = :bikeId 
            ORDER BY date DESC LIMIT 1
        )
        WHERE f1.bikeId = :bikeId AND f2.id IS NOT NULL
        ORDER BY f1.date ASC
    """)
    fun getFuelEfficiencyTrend(bikeId: Int): Flow<List<FuelEfficiencyData>>
}

// Data class for monthly fuel summary
data class MonthlyFuelSummary(
    val year: String,
    val month: String,
    val totalCost: Double,
    val totalLiters: Double
)

// Data class for fuel efficiency trend
data class FuelEfficiencyData(
    val id: Int,
    val date: Date,
    val liters: Double,
    val odometer: Int,
    val distance: Int,
    val mileage: Double
)