package com.example.bikeexpensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bikeId: Int,
    val startOdometer: Int,
    val endOdometer: Int,
    val startDate: Date,
    val endDate: Date? = null,
    val purpose: String = "",
    val note: String = "",
    val isCompleted: Boolean = false
)