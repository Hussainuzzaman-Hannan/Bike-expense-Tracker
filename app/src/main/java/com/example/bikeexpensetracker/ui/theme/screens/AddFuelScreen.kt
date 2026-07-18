@file:OptIn(ExperimentalAnimationApi::class)

package com.example.bikeexpensetracker.ui.screens

import com.example.bikeexpensetracker.data.SelectedBikeManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikeexpensetracker.model.FuelEntry
import com.example.bikeexpensetracker.viewmodel.FuelViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFuelScreen(
    onFuelSaved: () -> Unit,
    viewModel: FuelViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Form state
    var liters by remember { mutableStateOf("") }
    var pricePerLiter by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isFullTank by remember { mutableStateOf(true) }

    // Validation states
    var litersError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var odometerError by remember { mutableStateOf<String?>(null) }

    // Last fuel entry for mileage calculation
    val lastFuelEntry by viewModel.lastFuelEntry.collectAsState(initial = null)
    val isSaving by viewModel.isSaving.collectAsState()

    // Calculate values
    val totalCost = calculateTotalCost(liters, pricePerLiter)
    val calculatedMileage = calculateMileage(
        currentOdometer = odometer.toIntOrNull(),
        currentLiters = liters.toDoubleOrNull(),
        lastEntry = lastFuelEntry
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Fuel Record",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onFuelSaved) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
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
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.LocalGasStation,
                            contentDescription = "Fuel",
                            modifier = Modifier.size(56.dp),
                            tint = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Record Fuel Purchase",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Track your fuel expenses and calculate mileage",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Form Fields Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Fuel Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Liters Input
                    OutlinedTextField(
                        value = liters,
                        onValueChange = {
                            liters = it.filter { char -> char.isDigit() || char == '.' }
                            litersError = null
                        },
                        label = { Text("Liters") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.LocalGasStation,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        isError = litersError != null,
                        supportingText = {
                            if (litersError != null) {
                                Text(
                                    litersError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price per Liter Input
                    OutlinedTextField(
                        value = pricePerLiter,
                        onValueChange = {
                            pricePerLiter = it.filter { char -> char.isDigit() || char == '.' }
                            priceError = null
                        },
                        label = { Text("Price per Liter (৳)") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        isError = priceError != null,
                        supportingText = {
                            if (priceError != null) {
                                Text(
                                    priceError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Odometer Input
                    OutlinedTextField(
                        value = odometer,
                        onValueChange = {
                            odometer = it.filter { char -> char.isDigit() }
                            odometerError = null
                        },
                        label = { Text("Odometer Reading (km)") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Speed,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        isError = odometerError != null,
                        supportingText = {
                            if (odometerError != null) {
                                Text(
                                    odometerError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Full Tank Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Full Tank",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Check if this was a full tank refill",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = isFullTank,
                            onCheckedChange = { isFullTank = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Note Input
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note (Optional)") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        minLines = 2,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Mileage Card (shows calculated mileage)
            if (calculatedMileage != null && calculatedMileage > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Speed,
                            contentDescription = "Mileage",
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Estimated Mileage",
                                fontSize = 12.sp,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                text = String.format(Locale.US, "%.2f km/l", calculatedMileage),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                text = "Based on previous fuel record",
                                fontSize = 10.sp,
                                color = Color(0xFF4CAF50).copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Total Cost Card
            if (totalCost > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Cost",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = String.format(Locale.US, "৳%.2f", totalCost),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Date",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            // Save Button with Loading State
            Button(
                onClick = {
                    if (validateForm(liters, pricePerLiter, odometer)) {
                        scope.launch {
                            val fuelEntry = FuelEntry(
                                liters = liters.toDouble(),
                                pricePerLiter = pricePerLiter.toDouble(),
                                totalCost = liters.toDouble() * pricePerLiter.toDouble(),
                                odometer = odometer.toInt(),
                                date = Date(),
                                note = note,
                                isFullTank = isFullTank,
                                bikeId = SelectedBikeManager.getSelectedBikeId()
                            )
                            viewModel.addFuelEntry(fuelEntry)
                            onFuelSaved()
                        }
                    } else {
                        if (liters.isEmpty()) litersError = "Liters is required"
                        if (pricePerLiter.isEmpty()) priceError = "Price is required"
                        if (odometer.isEmpty()) odometerError = "Odometer is required"
                    }
                },
                enabled = !isSaving && isFormValid(liters, pricePerLiter, odometer),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saving...", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Fuel Record", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// Helper function to calculate total cost
private fun calculateTotalCost(liters: String, price: String): Double {
    val litersValue = liters.toDoubleOrNull() ?: return 0.0
    val priceValue = price.toDoubleOrNull() ?: return 0.0
    return litersValue * priceValue
}

// Helper function to calculate mileage
private fun calculateMileage(
    currentOdometer: Int?,
    currentLiters: Double?,
    lastEntry: FuelEntry?
): Double? {
    if (lastEntry == null || currentOdometer == null || currentLiters == null) {
        return null
    }

    val distance = currentOdometer - lastEntry.odometer
    if (distance <= 0 || currentLiters <= 0) {
        return null
    }

    return distance.toDouble() / currentLiters
}

// Form validation
private fun validateForm(liters: String, price: String, odometer: String): Boolean {
    var isValid = true

    if (liters.isEmpty()) {
        isValid = false
    } else {
        val litersValue = liters.toDoubleOrNull()
        if (litersValue == null || litersValue <= 0) {
            isValid = false
        }
    }

    if (price.isEmpty()) {
        isValid = false
    } else {
        val priceValue = price.toDoubleOrNull()
        if (priceValue == null || priceValue <= 0) {
            isValid = false
        }
    }

    if (odometer.isEmpty()) {
        isValid = false
    } else {
        val odometerValue = odometer.toIntOrNull()
        if (odometerValue == null || odometerValue <= 0) {
            isValid = false
        }
    }

    return isValid
}

private fun isFormValid(liters: String, price: String, odometer: String): Boolean {
    val litersValid = liters.toDoubleOrNull()?.let { it > 0 } ?: false
    val priceValid = price.toDoubleOrNull()?.let { it > 0 } ?: false
    val odometerValid = odometer.toIntOrNull()?.let { it > 0 } ?: false
    return litersValid && priceValid && odometerValid
}