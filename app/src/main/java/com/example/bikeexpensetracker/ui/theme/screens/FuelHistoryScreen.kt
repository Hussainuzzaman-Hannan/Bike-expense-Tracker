package com.example.bikeexpensetracker.ui.screens

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikeexpensetracker.model.FuelEntry
import com.example.bikeexpensetracker.viewmodel.FuelViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelHistoryScreen(
    onBackClick: () -> Unit,
    viewModel: FuelViewModel = viewModel()
) {
    val fuelEntries by viewModel.allFuelEntries.collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<FuelEntry?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fuel History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    // Optional: Add stats button
                    IconButton(onClick = { /* Navigate to fuel stats */ }) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = "Statistics",
                            tint = Color.White
                        )
                    }
                }
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
            // Header with summary
            if (fuelEntries.isNotEmpty()) {
                FuelSummaryHeader(entries = fuelEntries)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Fuel entries list
            if (fuelEntries.isEmpty()) {
                EmptyFuelHistory()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(fuelEntries) { entry ->
                        FuelHistoryItem(
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
            title = { Text("Delete Fuel Record") },
            text = {
                Text("Are you sure you want to delete this fuel record?\n\n" +
                        "Date: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedEntry!!.date)}\n" +
                        "Amount: ${selectedEntry!!.liters} L\n" +
                        "Cost: ৳${String.format("%.2f", selectedEntry!!.totalCost)}")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedEntry?.let { viewModel.deleteFuelEntry(it) }
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
fun FuelSummaryHeader(entries: List<FuelEntry>) {
    val totalLiters = entries.sumOf { it.liters }
    val totalCost = entries.sumOf { it.totalCost }
    val totalDistance = calculateTotalDistance(entries)
    val averageMileage = calculateAverageMileageFromEntries(entries)

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Fuel Summary",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryStat(
                    label = "Total Fuel",
                    value = String.format("%.1f", totalLiters),
                    unit = "L",
                    color = Color(0xFF2196F3)
                )
                SummaryStat(
                    label = "Total Cost",
                    value = String.format("%.0f", totalCost),
                    unit = "৳",
                    color = Color(0xFF4CAF50)
                )
                SummaryStat(
                    label = "Avg. Mileage",
                    value = String.format("%.1f", averageMileage),
                    unit = "km/L",
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun SummaryStat(
    label: String,
    value: String,
    unit: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = unit,
                fontSize = 11.sp,
                color = color.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
            )
        }
    }
}

@Composable
fun FuelHistoryItem(
    entry: FuelEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()  // This now works with the import
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
            // Header with date and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocalGasStation,
                        contentDescription = "Fuel",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(entry.date),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
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

            // Fuel details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FuelDetailItem(
                    label = "Liters",
                    value = String.format("%.2f", entry.liters),
                    unit = "L",
                    icon = Icons.Default.LocalGasStation
                )
                FuelDetailItem(
                    label = "Price/L",
                    value = String.format("%.2f", entry.pricePerLiter),
                    unit = "৳",
                    icon = Icons.Default.AttachMoney
                )
                FuelDetailItem(
                    label = "Total",
                    value = String.format("%.2f", entry.totalCost),
                    unit = "৳",
                    icon = Icons.Default.CurrencyRupee
                )
                FuelDetailItem(
                    label = "Odometer",
                    value = entry.odometer.toString(),
                    unit = "km",
                    icon = Icons.Default.Speed
                )
            }

            // Note if exists
            if (entry.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
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
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun FuelDetailItem(
    label: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun EmptyFuelHistory() {
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
                Icons.Default.LocalGasStation,
                contentDescription = "No Fuel Records",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            Text(
                text = "No Fuel Records Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tap the + button and select Fuel\nto add your first fuel record",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

// Helper functions
private fun calculateTotalDistance(entries: List<FuelEntry>): Int {
    if (entries.size < 2) return 0
    val firstOdometer = entries.last().odometer
    val lastOdometer = entries.first().odometer
    return lastOdometer - firstOdometer
}

private fun calculateAverageMileageFromEntries(entries: List<FuelEntry>): Double {
    if (entries.size < 2) return 0.0

    var totalDistance = 0.0
    var totalLiters = 0.0

    for (i in 0 until entries.size - 1) {
        val current = entries[i]
        val previous = entries[i + 1]
        val distance = current.odometer - previous.odometer
        if (distance > 0 && current.liters > 0) {
            totalDistance += distance
            totalLiters += current.liters
        }
    }

    return if (totalLiters > 0) totalDistance / totalLiters else 0.0
}