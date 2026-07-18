package com.example.bikeexpensetracker

import android.app.Application
import com.example.bikeexpensetracker.utils.ReminderManager

class BikeExpenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize reminder manager and schedule checks
        ReminderManager.getInstance(this).scheduleReminderCheck()
    }
}