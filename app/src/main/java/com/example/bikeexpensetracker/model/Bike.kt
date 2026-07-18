package com.example.bikeexpensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "bikes")
data class Bike(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val model: String = "",
    val brand: String = "",
    val totalKm: Int = 0,
    val purchaseDate: Date? = null,
    val registrationNumber: String = "",
    val isActive: Boolean = true,
    val initialOdometer: Int = 0,
    val lastServiceDate: Date? = null,
    val lastServiceOdometer: Int = 0,
    val imageUri: String = ""
) {
    // Computed property for total distance traveled
    val distanceTraveled: Int
        get() = totalKm - initialOdometer

    // Computed property for bike age in months
    val ageInMonths: Int
        get() = purchaseDate?.let {
            val now = Date()
            val diff = now.time - it.time
            (diff / (1000 * 60 * 60 * 24 * 30.44)).toInt()
        } ?: 0
}