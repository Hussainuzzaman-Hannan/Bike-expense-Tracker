package com.example.bikeexpensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "maintenance_entries")
data class MaintenanceEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val cost: Double,
    val date: Date,
    val odometerAtService: Int = 0,
    val note: String = "",
    val bikeId: Int = 1,
    val category: MaintenanceCategory = MaintenanceCategory.OTHER,
    val isRecurring: Boolean = false,
    val nextDueDate: Date? = null,
    val nextDueOdometer: Int? = null
)

enum class MaintenanceCategory {
    ENGINE_OIL,
    BRAKE_PADS,
    TIRES,
    CHAIN_SPROCKET,
    AIR_FILTER,
    SPARK_PLUG,
    BATTERY,
    INSURANCE,
    TAX,
    OTHER
}