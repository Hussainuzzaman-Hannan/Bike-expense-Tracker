package com.example.bikeexpensetracker.data.dao

import androidx.room.*
import com.example.bikeexpensetracker.model.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE bikeId = :bikeId ORDER BY startDate DESC")
    fun getTripsByBike(bikeId: Int): Flow<List<Trip>>

    @Insert
    suspend fun insertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Query("SELECT SUM(endOdometer - startOdometer) FROM trips WHERE bikeId = :bikeId AND isCompleted = 1")
    fun getTotalTripDistance(bikeId: Int): Flow<Int>
}