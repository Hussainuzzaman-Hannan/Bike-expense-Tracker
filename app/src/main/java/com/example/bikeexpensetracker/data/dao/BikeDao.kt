package com.example.bikeexpensetracker.data.dao

import androidx.room.*
import com.example.bikeexpensetracker.model.Bike
import kotlinx.coroutines.flow.Flow

@Dao
interface BikeDao {
    @Query("SELECT * FROM bikes WHERE isActive = 1")
    fun getActiveBikes(): Flow<List<Bike>>

    @Query("SELECT * FROM bikes WHERE id = :bikeId")
    suspend fun getBikeById(bikeId: Int): Bike?

    @Insert
    suspend fun insertBike(bike: Bike): Long

    @Update
    suspend fun updateBike(bike: Bike)

    @Query("UPDATE bikes SET totalKm = totalKm + :km WHERE id = :bikeId")
    suspend fun updateTotalKm(bikeId: Int, km: Int)
}