package com.example.bikeexpensetracker

import android.app.Application
import com.example.bikeexpensetracker.data.SelectedBikeManager
import com.example.bikeexpensetracker.utils.ReminderManager

class BikeExpenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the selected-bike tracker so it's ready before any ViewModel is created
        SelectedBikeManager.init(this)

        // Initialize reminder manager and schedule checks
        ReminderManager.getInstance(this).scheduleReminderCheck()
    }
}