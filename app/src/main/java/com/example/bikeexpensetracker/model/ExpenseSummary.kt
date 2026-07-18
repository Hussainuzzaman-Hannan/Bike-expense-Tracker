package com.example.bikeexpensetracker.model

import java.util.Date

data class ExpenseSummary(
    val totalFuelCost: Double = 0.0,
    val totalMaintenanceCost: Double = 0.0,
    val totalFuelLiters: Double = 0.0,
    val averageFuelEfficiency: Double = 0.0,
    val totalDistance: Int = 0,
    val totalExpenses: Double = 0.0,
    val costPerKm: Double = 0.0,
    val monthlyAverage: Double = 0.0,
    val lastMaintenanceDate: Date? = null,
    val nextMaintenanceDue: Date? = null
)

data class MonthlyExpense(
    val year: Int,
    val month: Int,
    val fuelCost: Double,
    val maintenanceCost: Double,
    val totalCost: Double,
    val distanceTraveled: Int
)

data class FuelEfficiencyStats(
    val averageEfficiency: Double,
    val bestEfficiency: Double,
    val worstEfficiency: Double,
    val lastEfficiency: Double,
    val efficiencyTrend: Double // Percentage change
)