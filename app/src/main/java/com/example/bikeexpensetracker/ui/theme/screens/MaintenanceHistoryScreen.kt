package com.example.bikeexpensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikeexpensetracker.model.MaintenanceCategory
import com.example.bikeexpensetracker.model.MaintenanceEntry
import com.example.bikeexpensetracker.viewmodel.MaintenanceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceHistoryScreen(
    onBackClick: () -> Unit,
    viewModel: MaintenanceViewModel = viewModel()
) {
    val maintenanceEntries by viewModel.allMaintenanceEntries.collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<MaintenanceEntry?>(null) }

    val totalCost = maintenanceEntries.sumOf { it.cost }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Maintenance History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            // Summary Header
            if (maintenanceEntries.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SummaryStat(
                            label = "Total Services",
                            value = maintenanceEntries.size.toString(),
                            unit = "",
                            color = Color(0xFF2196F3)
                        )
                        SummaryStat(
                            label = "Total Cost",
                            value = String.format("%.0f", totalCost),
                            unit = "৳",
                            color = Color(0xFF4CAF50)
                        )
                        SummaryStat(
                            label = "Categories",
                            value = maintenanceEntries.distinctBy { it.category }.size.toString(),
                            unit = "",
                            color = Color(0xFFFF9800)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Maintenance List
            if (maintenanceEntries.isEmpty()) {
                EmptyMaintenanceHistory()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(maintenanceEntries) { entry ->
                        MaintenanceHistoryItem(
                            entry = entry,
                            onDelete = {
                                selectedEntry = entry
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && selectedEntry != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Maintenance Record") },
            text = {
                Text("Are you sure you want to delete this maintenance record?\n\n" +
                        "Service: ${selectedEntry!!.title}\n" +
                        "Cost: ৳${String.format("%.2f", selectedEntry!!.cost)}\n" +
                        "Date: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedEntry!!.date)}")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedEntry?.let { viewModel.deleteMaintenanceEntry(it) }
                        showDeleteDialog = false
                        selectedEntry = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MaintenanceHistoryItem(
    entry: MaintenanceEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with category and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(getMaintenanceCategoryColor(entry.category).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getMaintenanceCategoryIcon(entry.category),
                            contentDescription = entry.category.name,
                            modifier = Modifier.size(24.dp),
                            tint = getMaintenanceCategoryColor(entry.category)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = entry.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = entry.category.name.replace("_", " "),
                            fontSize = 11.sp,
                            color = getMaintenanceCategoryColor(entry.category)
                        )
                    }
                }

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

            Spacer(modifier = Modifier.height(12.dp))

            // Cost and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = "Cost",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "৳${String.format("%.2f", entry.cost)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "Date",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(entry.date),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Odometer if available
            if (entry.odometerAtService > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Speed,
                        contentDescription = "Odometer",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Odometer: ${entry.odometerAtService} km",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Note if exists
            if (entry.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Note",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = entry.note,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyMaintenanceHistory() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Build,
                contentDescription = "No Maintenance Records",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            Text(
                text = "No Maintenance Records Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tap the + button and select Maintenance\nto add your first maintenance record",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

// Helper functions for Maintenance Category
fun getMaintenanceCategoryColor(category: MaintenanceCategory): Color {
    return when (category) {
        MaintenanceCategory.ENGINE_OIL -> Color(0xFF795548)
        MaintenanceCategory.BRAKE_PADS -> Color(0xFFF44336)
        MaintenanceCategory.TIRES -> Color(0xFF4CAF50)
        MaintenanceCategory.CHAIN_SPROCKET -> Color(0xFFFF9800)
        MaintenanceCategory.AIR_FILTER -> Color(0xFF2196F3)
        MaintenanceCategory.SPARK_PLUG -> Color(0xFF9C27B0)
        MaintenanceCategory.BATTERY -> Color(0xFF00BCD4)
        MaintenanceCategory.INSURANCE -> Color(0xFF3F51B5)
        MaintenanceCategory.TAX -> Color(0xFF607D8B)
        MaintenanceCategory.OTHER -> Color(0xFF9E9E9E)
    }
}

@Composable
fun getMaintenanceCategoryIcon(category: MaintenanceCategory): ImageVector {
    return when (category) {
        MaintenanceCategory.ENGINE_OIL -> Icons.Default.OilBarrel
        MaintenanceCategory.BRAKE_PADS -> Icons.Default.Build
        MaintenanceCategory.TIRES -> Icons.Default.Route
        MaintenanceCategory.CHAIN_SPROCKET -> Icons.Default.Settings
        MaintenanceCategory.AIR_FILTER -> Icons.Default.FilterAlt
        MaintenanceCategory.SPARK_PLUG -> Icons.Default.FlashOn
        MaintenanceCategory.BATTERY -> Icons.Default.BatteryFull
        MaintenanceCategory.INSURANCE -> Icons.Default.Security
        MaintenanceCategory.TAX -> Icons.Default.Receipt
        MaintenanceCategory.OTHER -> Icons.Default.Category
    }
}