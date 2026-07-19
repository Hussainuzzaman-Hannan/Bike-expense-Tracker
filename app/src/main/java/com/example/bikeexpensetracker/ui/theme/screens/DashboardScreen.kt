@file:OptIn(ExperimentalAnimationApi::class)

package com.example.bikeexpensetracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikeexpensetracker.model.Expense
import com.example.bikeexpensetracker.model.ExpenseCategory
import com.example.bikeexpensetracker.viewmodel.BikeViewModel
import com.example.bikeexpensetracker.viewmodel.ExpenseViewModel
import com.example.bikeexpensetracker.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    bikeViewModel: BikeViewModel = viewModel(),
    onAddFuelClick: () -> Unit = {},
    onAddOtherExpenseClick: () -> Unit = {},
    onViewMaintenanceHistoryClick: () -> Unit = {},
    onManageBikesClick: () -> Unit = {}
) {
    val expenses by viewModel.expenses.collectAsState()
    val isAddingExpense by viewModel.isAddingExpense.collectAsState()

    // Collect real-time data from ViewModel - with null safety
    val totalFuelCost by viewModel.totalFuelCost.collectAsState(initial = 0.0)
    val totalMaintenanceCost by viewModel.totalMaintenanceCost.collectAsState(initial = 0.0)
    val averageMileage by viewModel.averageMileage.collectAsState(initial = 0.0)

    // Get selected bike from BikeViewModel (source of truth for the active bike)
    val selectedBike by bikeViewModel.selectedBike.collectAsState()
    val bikeName = selectedBike?.name ?: "My Bike"

    // State for FAB expansion
    var fabExpanded by remember { mutableStateOf(false) }

    // Calculate other expenses with safe defaults
    val accessoriesCost = viewModel.getCategoryTotal(ExpenseCategory.ACCESSORIES)
    val otherCost = viewModel.getCategoryTotal(ExpenseCategory.OTHER)
    val totalExpenses = totalFuelCost + totalMaintenanceCost + accessoriesCost + otherCost

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bike Expense Tracker",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = onManageBikesClick) {
                        Icon(
                            Icons.Default.DirectionsBike,
                            contentDescription = "Manage Bikes",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            // Animated FAB with Menu
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Animated FAB Menu Items
                AnimatedVisibility(
                    visible = fabExpanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Fuel Option
                        Box(contentAlignment = Alignment.CenterEnd) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Fuel",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                                FloatingActionButton(
                                    onClick = {
                                        fabExpanded = false
                                        onAddFuelClick()
                                    },
                                    modifier = Modifier.size(48.dp),
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LocalGasStation,
                                        contentDescription = "Add Fuel",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Maintenance Option
                        Box(contentAlignment = Alignment.CenterEnd) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Maintenance",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                                FloatingActionButton(
                                    onClick = {
                                        fabExpanded = false
                                        onAddOtherExpenseClick()
                                    },
                                    modifier = Modifier.size(48.dp),
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Build,
                                        contentDescription = "Add Maintenance",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Main FAB
                FloatingActionButton(
                    onClick = { fabExpanded = !fabExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    ),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        if (fabExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = if (fabExpanded) "Close" else "Add",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Hero Section with Bike Info
            item {
                BikeInfoHeader(
                    totalDistance = selectedBike?.totalKm ?: 0,
                    averageMileage = averageMileage,
                    bikeName = bikeName
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Summary Cards Section with Real Data
            item {
                SummaryCardsSection(
                    fuelCost = totalFuelCost,
                    maintenanceCost = totalMaintenanceCost,
                    averageMileage = averageMileage,
                    totalExpenses = totalExpenses
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Recent Expenses Section
            item {
                RecentExpensesSection(
                    expenses = expenses.take(5),
                    onDelete = { viewModel.removeExpense(it) },
                    onViewAllMaintenance = onViewMaintenanceHistoryClick
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Insights Section
            item {
                InsightsSection(
                    expenses = expenses,
                    fuelCost = totalFuelCost,
                    maintenanceCost = totalMaintenanceCost
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Add Expense Dialog (for other expenses)
    if (isAddingExpense) {
        AddExpenseDialog(
            onDismiss = { viewModel.toggleAddExpenseDialog() },
            onAddExpense = { title, amount, category, description ->
                viewModel.addExpense(title, amount, category, description)
                viewModel.toggleAddExpenseDialog()
            }
        )
    }
}

@Composable
fun BikeInfoHeader(
    totalDistance: Int = 2450,
    averageMileage: Double = 35.5,
    bikeName: String = "Yamaha R15 V4"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bike Icon with Animation
                AnimatedContent(
                    targetState = true,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) with
                                fadeOut(animationSpec = tween(500))
                    },
                    label = "bike_icon"
                ) { _ ->
                    Icon(
                        Icons.Default.DirectionsBike,
                        contentDescription = "Bike",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                CircleShape
                            )
                            .padding(12.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = bikeName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Speed,
                        contentDescription = "Mileage",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Current Mileage: ${String.format(Locale.US, "%.1f", averageMileage)} km/l",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatChip(
                        icon = Icons.Outlined.CalendarToday,
                        label = "Since",
                        value = "Jan 2024"
                    )
                    StatChip(
                        icon = Icons.Outlined.Route,
                        label = "Total KM",
                        value = formatDistance(totalDistance)
                    )
                    StatChip(
                        icon = Icons.Outlined.Timeline,
                        label = "Avg/Day",
                        value = "25 km"
                    )
                }
            }
        }
    }
}

private fun formatDistance(distance: Int): String {
    return if (distance >= 1000) {
        String.format(Locale.US, "%.1fK", distance / 1000.0)
    } else {
        "$distance"
    }
}

@Composable
fun StatChip(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun SummaryCardsSection(
    fuelCost: Double,
    maintenanceCost: Double,
    averageMileage: Double,
    totalExpenses: Double
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Expense Overview",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // First Row - Two Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Fuel Cost",
                amount = fuelCost,
                icon = Icons.Default.LocalGasStation,
                iconColor = Color(0xFF4CAF50),
                gradient = listOf(Color(0xFF4CAF50), Color(0xFF81C784)),
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = "Maintenance",
                amount = maintenanceCost,
                icon = Icons.Default.Build,
                iconColor = Color(0xFFFF9800),
                gradient = listOf(Color(0xFFFF9800), Color(0xFFFFB74D)),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Second Row - Two Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Avg. Mileage",
                amount = averageMileage,
                icon = Icons.Default.Speed,
                iconColor = Color(0xFF2196F3),
                gradient = listOf(Color(0xFF2196F3), Color(0xFF64B5F6)),
                isMileage = true,
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = "Total Spent",
                amount = totalExpenses,
                icon = Icons.Default.AttachMoney,
                iconColor = Color(0xFF9C27B0),
                gradient = listOf(Color(0xFF9C27B0), Color(0xFFBA68C8)),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    iconColor: Color,
    gradient: List<Color>,
    isMileage: Boolean = false,
    modifier: Modifier = Modifier
) {
    var animatedAmount by remember { mutableStateOf(0f) }

    LaunchedEffect(amount) {
        animate(
            initialValue = 0f,
            targetValue = amount.toFloat(),
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        ) { value, _ ->
            animatedAmount = value
        }
    }

    Card(
        modifier = modifier
            .height(140.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = gradient,
                        startX = 0.0f,
                        endX = 1.0f
                    ),
                    alpha = 0.1f
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = if (isMileage) "${String.format(Locale.US, "%.1f", animatedAmount)} km/l"
                        else "৳${String.format(Locale.US, "%.2f", animatedAmount)}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!isMileage) {
                        Text(
                            text = "Total spent",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentExpensesSection(
    expenses: List<Expense>,
    onDelete: (String) -> Unit,
    onViewAllMaintenance: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Expenses",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(
                onClick = onViewAllMaintenance
            ) {
                Text("Maintenance History", fontSize = 12.sp)
            }
        }

        if (expenses.isEmpty()) {
            EmptyStateCard()
        } else {
            expenses.forEach { expense ->
                ExpenseListItem(
                    expense = expense,
                    onDelete = { onDelete(expense.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ExpenseListItem(
    expense: Expense,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // Delete background
        if (offsetX < -50f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.error,
                                MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Delete", color = Color.White, fontSize = 14.sp)
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                }
            }
        }

        // Card content with swipe gesture
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            isDragging = false
                            if (offsetX < -200f) {
                                onDelete()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = {
                            isDragging = false
                            offsetX = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount).coerceIn(-250f, 0f)
                        }
                    )
                }
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated icon
                    AnimatedContent(
                        targetState = expense.category,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) with
                                    fadeOut(animationSpec = tween(300))
                        },
                        label = "category_icon"
                    ) { category ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    getCategoryColor(category).copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                getCategoryIcon(category),
                                contentDescription = category.name,
                                tint = getCategoryColor(category),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = expense.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(expense.date),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        if (expense.description.isNotEmpty()) {
                            Text(
                                text = expense.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "৳${String.format(Locale.US, "%.2f", expense.amount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InsightsSection(
    expenses: List<Expense>,
    fuelCost: Double,
    maintenanceCost: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = "Insights",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Spending Insights",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Spending Distribution
            val total = fuelCost + maintenanceCost
            val fuelPercentage = if (total > 0) (fuelCost / total) * 100 else 0.0
            val maintenancePercentage = if (total > 0) (maintenanceCost / total) * 100 else 0.0

            // Fuel Insight
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Fuel",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.1f", fuelPercentage)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = (fuelPercentage / 100).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Maintenance Insight
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Maintenance",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.1f", maintenancePercentage)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = (maintenancePercentage / 100).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFFF9800),
                    trackColor = Color(0xFFFF9800).copy(alpha = 0.2f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tip
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TipsAndUpdates,
                        contentDescription = "Tip",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (fuelPercentage > 50)
                            "Consider regular maintenance to improve fuel efficiency"
                        else "Great job keeping maintenance costs low!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Receipt,
                contentDescription = "No Expenses",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No expenses recorded yet",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap the + button to add your first expense",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onAddExpense: (String, Double, ExpenseCategory, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.MAINTENANCE) }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Filter out FUEL category
    val expenseCategories = ExpenseCategory.values().filter { it != ExpenseCategory.FUEL }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Expense") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = amount.isNotEmpty() && amount.toDoubleOrNull() == null
                )

                // Category Dropdown - Fixed Version
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        expenseCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (title.isNotBlank() && amountValue != null && amountValue > 0) {
                        onAddExpense(title, amountValue, selectedCategory, description)
                    }
                },
                enabled = title.isNotBlank() && amount.toDoubleOrNull() != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper Functions
fun getCategoryColor(category: ExpenseCategory): Color {
    return when (category) {
        ExpenseCategory.FUEL -> Color(0xFF4CAF50)
        ExpenseCategory.MAINTENANCE -> Color(0xFFFF9800)
        ExpenseCategory.INSURANCE -> Color(0xFF2196F3)
        ExpenseCategory.ACCESSORIES -> Color(0xFF9C27B0)
        ExpenseCategory.OTHER -> Color(0xFF607D8B)
    }
}

fun getCategoryIcon(category: ExpenseCategory): ImageVector {
    return when (category) {
        ExpenseCategory.FUEL -> Icons.Default.LocalGasStation
        ExpenseCategory.MAINTENANCE -> Icons.Default.Build
        ExpenseCategory.INSURANCE -> Icons.Default.Security
        ExpenseCategory.ACCESSORIES -> Icons.Default.ShoppingCart
        ExpenseCategory.OTHER -> Icons.Default.Category
    }
}

fun calculateAverageMileage(expenses: List<Expense>): Double {
    val fuelExpenses = expenses.filter { it.category == ExpenseCategory.FUEL }
    if (fuelExpenses.isEmpty()) return 0.0
    return 35.0
}