package com.example.bikeexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeexpensetracker.data.BikeExpenseDatabase
import com.example.bikeexpensetracker.model.FuelEntry
import com.example.bikeexpensetracker.ui.components.MileageDataPoint
import kotlinx.coroutines.flow.*
import java.util.*

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = BikeExpenseDatabase.getDatabase(application)

    // Get total fuel cost from database
    val totalFuelCost: StateFlow<Double> = database.fuelEntryDao().getTotalFuelCost(1)
        .catch { emit(0.0) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0.0
        )

    // Get total maintenance cost from database
    val totalMaintenanceCost: StateFlow<Double> = database.maintenanceDao().getTotalMaintenanceCost(1)
        .catch { emit(0.0) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0.0
        )

    // Get mileage trend data from database
    val mileageTrendData: StateFlow<List<MileageDataPoint>> = database.fuelEntryDao()
        .getAllFuelEntries()
        .map { entries ->
            calculateMileageDataPoints(entries)
        }
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Get monthly fuel summary from database
    val monthlyFuelSummary: StateFlow<List<MonthlySummary>> = database.fuelEntryDao()
        .getMonthlyFuelSummary(1)
        .catch { emit(emptyList()) }
        .map { summaries ->
            summaries.map { summary ->
                MonthlySummary(
                    year = summary.year,
                    month = summary.month,
                    fuelCost = summary.totalCost,
                    maintenanceCost = 0.0, // Can be extended if needed
                    totalCost = summary.totalCost
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private fun calculateMileageDataPoints(entries: List<FuelEntry>): List<MileageDataPoint> {
        if (entries.size < 2) return emptyList()

        val points = mutableListOf<MileageDataPoint>()

        for (i in 0 until entries.size - 1) {
            val current = entries[i]
            val previous = entries[i + 1]
            val distance = current.odometer - previous.odometer

            if (distance > 0 && current.liters > 0) {
                val mileage = distance.toDouble() / current.liters
                points.add(MileageDataPoint(current.date, mileage))
            }
        }

        return points.reversed() // Show chronological order
    }
}

// Data class for monthly summary
data class MonthlySummary(
    val year: String,
    val month: String,
    val fuelCost: Double,
    val maintenanceCost: Double,
    val totalCost: Double
)