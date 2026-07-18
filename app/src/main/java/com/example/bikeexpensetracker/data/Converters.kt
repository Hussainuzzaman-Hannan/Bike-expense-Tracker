package com.example.bikeexpensetracker.data

import androidx.room.TypeConverter
import com.example.bikeexpensetracker.model.MaintenanceCategory
import com.example.bikeexpensetracker.model.ReminderType
import java.util.Date

class Converters {

    // Date converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // MaintenanceCategory converters
    @TypeConverter
    fun fromMaintenanceCategory(category: MaintenanceCategory): String {
        return category.name
    }

    @TypeConverter
    fun toMaintenanceCategory(name: String): MaintenanceCategory {
        return MaintenanceCategory.valueOf(name)
    }

    // ReminderType converters
    @TypeConverter
    fun fromReminderType(type: ReminderType): String {
        return type.name
    }

    @TypeConverter
    fun toReminderType(name: String): ReminderType {
        return ReminderType.valueOf(name)
    }
}