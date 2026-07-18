package com.example.bikeexpensetracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object AddFuel : Screen("add_fuel", "Add Fuel", Icons.Default.LocalGasStation)
    object FuelHistory : Screen("fuel_history", "Fuel History", Icons.Default.History)
    object Maintenance : Screen("maintenance", "Maintenance", Icons.Default.Build)
    object MaintenanceHistory : Screen("maintenance_history", "Maintenance History", Icons.Default.History)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.BarChart)
    object Reminders : Screen("reminders", "Reminders", Icons.Default.Notifications)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)  // Settings added
    object BikeProfiles : Screen("bike_profiles", "My Bikes", Icons.Default.DirectionsBike)
}

val screens = listOf(
    Screen.Home,
    Screen.AddFuel,
    Screen.FuelHistory,
    Screen.Maintenance,
    Screen.Analytics,
    Screen.Reminders,
    Screen.Settings  // Settings added to bottom navigation
)