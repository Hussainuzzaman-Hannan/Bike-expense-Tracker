package com.example.bikeexpensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * FuelEntry Entity - Represents a fuel purchase record
 * This will create a table named "fuel_entries" in the database
 */
@Entity(tableName = "fuel_entries")
data class FuelEntry(
    @PrimaryKey(autoGenerate = true)  // Auto-generates unique ID for each record
    val id: Int = 0,

    val liters: Double,              // Amount of fuel purchased
    val pricePerLiter: Double,       // Price per liter at time of purchase
    val totalCost: Double,           // Total cost = liters × pricePerLiter

    val odometer: Int,               // Current odometer reading
    val date: Date,                  // Date of fuel purchase
    val bikeId: Int = 1,             // For multiple bikes support
    val note: String = "",           // Optional notes
    val isFullTank: Boolean = true   // Whether this was a full tank refill
)