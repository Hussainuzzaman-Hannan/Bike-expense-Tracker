package com.example.bikeexpensetracker.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bikeexpensetracker.navigation.Screen
import com.example.bikeexpensetracker.navigation.screens
import com.example.bikeexpensetracker.ui.screens.*
import com.example.bikeexpensetracker.viewmodel.AnalyticsViewModel
import com.example.bikeexpensetracker.viewmodel.ReminderViewModel
import com.example.bikeexpensetracker.viewmodel.SettingsViewModel

// Animation specifications
private val enterTransition = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(300)
) + fadeIn(animationSpec = tween(300))

private val exitTransition = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(300)
) + fadeOut(animationSpec = tween(300))

private val popEnterTransition = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(300)
) + fadeIn(animationSpec = tween(300))

private val popExitTransition = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(300)
) + fadeOut(animationSpec = tween(300))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    expenseViewModel: com.example.bikeexpensetracker.viewmodel.ExpenseViewModel,
    fuelViewModel: com.example.bikeexpensetracker.viewmodel.FuelViewModel,
    maintenanceViewModel: com.example.bikeexpensetracker.viewmodel.MaintenanceViewModel,
    analyticsViewModel: AnalyticsViewModel,
    reminderViewModel: ReminderViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val currentRoute = currentRoute(navController)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Home/Dashboard Screen
            composable(
                route = Screen.Home.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                DashboardScreen(
                    viewModel = expenseViewModel,
                    settingsViewModel = settingsViewModel,
                    onAddFuelClick = {
                        navController.navigate(Screen.AddFuel.route)
                    },
                    onAddOtherExpenseClick = {
                        navController.navigate(Screen.Maintenance.route)
                    },
                    onViewMaintenanceHistoryClick = {
                        navController.navigate(Screen.MaintenanceHistory.route)
                    }
                )
            }

            // Add Fuel Screen
            composable(
                route = Screen.AddFuel.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                AddFuelScreen(
                    viewModel = fuelViewModel,
                    onFuelSaved = {
                        navController.popBackStack()
                    }
                )
            }

            // Fuel History Screen
            composable(
                route = Screen.FuelHistory.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                FuelHistoryScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = fuelViewModel
                )
            }

            // Add Maintenance Screen
            composable(
                route = Screen.Maintenance.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                AddMaintenanceScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveSuccess = {
                        navController.popBackStack()
                    },
                    onViewHistory = {
                        navController.navigate(Screen.MaintenanceHistory.route)
                    },
                    viewModel = maintenanceViewModel
                )
            }

            // Maintenance History Screen
            composable(
                route = Screen.MaintenanceHistory.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                MaintenanceHistoryScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = maintenanceViewModel
                )
            }

            // Analytics Screen
            composable(
                route = Screen.Analytics.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                AnalyticsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = analyticsViewModel
                )
            }

            // Reminders Screen
            composable(
                route = Screen.Reminders.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                ReminderSettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = reminderViewModel
                )
            }

            // Settings Screen
            composable(
                route = Screen.Settings.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                SettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = settingsViewModel
                )
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}