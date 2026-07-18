package com.example.bikeexpensetracker.model

import java.util.Date

sealed class CombinedExpense {
    abstract val id: Int
    abstract val date: Date
    abstract val cost: Double
    abstract val bikeId: Int

    data class Fuel(
        override val id: Int,
        override val date: Date,
        override val cost: Double,
        override val bikeId: Int,
        val liters: Double,
        val pricePerLiter: Double,
        val odometer: Int,
        val note: String
    ) : CombinedExpense()

    data class Maintenance(
        override val id: Int,
        override val date: Date,
        override val cost: Double,
        override val bikeId: Int,
        val title: String,
        val odometerAtService: Int,
        val note: String,
        val category: MaintenanceCategory
    ) : CombinedExpense()
}