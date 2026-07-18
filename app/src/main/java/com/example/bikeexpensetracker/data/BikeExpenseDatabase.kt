package com.example.bikeexpensetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bikeexpensetracker.model.*
import com.example.bikeexpensetracker.data.dao.*

@Database(
    entities = [
        Bike::class,
        FuelEntry::class,
        MaintenanceEntry::class,
        MaintenanceReminder::class,
        Trip::class
    ],
    version = 3,  // Incremented to version 3 for reminders feature
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BikeExpenseDatabase : RoomDatabase() {
    abstract fun bikeDao(): BikeDao
    abstract fun fuelEntryDao(): FuelEntryDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun maintenanceReminderDao(): MaintenanceReminderDao
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: BikeExpenseDatabase? = null

        fun getDatabase(context: Context): BikeExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BikeExpenseDatabase::class.java,
                    "bike_expense_database"
                ).fallbackToDestructiveMigration()  // This will recreate the database on version change
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}