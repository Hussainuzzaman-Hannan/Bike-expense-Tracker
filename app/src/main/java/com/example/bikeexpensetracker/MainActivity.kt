package com.example.bikeexpensetracker

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikeexpensetracker.ui.MainScreen
import com.example.bikeexpensetracker.ui.theme.BikeExpenseTrackerTheme
import com.example.bikeexpensetracker.viewmodel.AnalyticsViewModel
import com.example.bikeexpensetracker.viewmodel.BikeViewModel
import com.example.bikeexpensetracker.viewmodel.BikeViewModelFactory
import com.example.bikeexpensetracker.viewmodel.ExpenseViewModel
import com.example.bikeexpensetracker.viewmodel.FuelViewModel
import com.example.bikeexpensetracker.viewmodel.MaintenanceViewModel
import com.example.bikeexpensetracker.viewmodel.ReminderViewModel
import com.example.bikeexpensetracker.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BikeExpenseTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BikeExpenseApp()
                }
            }
        }
    }
}

@Composable
fun BikeExpenseApp() {
    val context = LocalContext.current.applicationContext as Application

    // Create all ViewModels with factories
    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(context)
    )
    val fuelViewModel: FuelViewModel = viewModel(
        factory = FuelViewModelFactory(context)
    )
    val maintenanceViewModel: MaintenanceViewModel = viewModel(
        factory = MaintenanceViewModelFactory(context)
    )
    val analyticsViewModel: AnalyticsViewModel = viewModel(
        factory = AnalyticsViewModelFactory(context)
    )
    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(context)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    val bikeViewModel: BikeViewModel = viewModel(
        factory = BikeViewModelFactory(context)
    )

    // Use MainScreen with bottom navigation
    MainScreen(
        expenseViewModel = expenseViewModel,
        fuelViewModel = fuelViewModel,
        maintenanceViewModel = maintenanceViewModel,
        analyticsViewModel = analyticsViewModel,
        reminderViewModel = reminderViewModel,
        settingsViewModel = settingsViewModel,
        bikeViewModel = bikeViewModel
    )
}

// ViewModel Factory for ExpenseViewModel
class ExpenseViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// ViewModel Factory for FuelViewModel
class FuelViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FuelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FuelViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// ViewModel Factory for MaintenanceViewModel
class MaintenanceViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaintenanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MaintenanceViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// ViewModel Factory for AnalyticsViewModel
class AnalyticsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalyticsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// ViewModel Factory for ReminderViewModel
class ReminderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// ViewModel Factory for SettingsViewModel
class SettingsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}