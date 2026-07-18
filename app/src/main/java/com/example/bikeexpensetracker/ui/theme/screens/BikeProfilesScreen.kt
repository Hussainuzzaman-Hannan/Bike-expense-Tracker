package com.example.bikeexpensetracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikeexpensetracker.model.Bike
import com.example.bikeexpensetracker.viewmodel.BikeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeProfilesScreen(
    onBackClick: () -> Unit,
    viewModel: BikeViewModel = viewModel()
) {
    val bikes by viewModel.bikes.collectAsState()
    val selectedBikeId by viewModel.selectedBikeId.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var bikeBeingEdited by remember { mutableStateOf<Bike?>(null) }
    var bikePendingDelete by remember { mutableStateOf<Bike?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Bikes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    bikeBeingEdited = null
                    showAddEditDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Bike", tint = Color.White)
            }
        }
    ) { paddingValues ->
        if (bikes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No bikes yet. Tap + to add one.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Tap a bike to make it active. All fuel, maintenance and analytics screens will show data for the active bike.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(bikes, key = { it.id }) { bike ->
                    BikeProfileCard(
                        bike = bike,
                        isSelected = bike.id == selectedBikeId,
                        canDelete = bikes.size > 1,
                        onSelect = { viewModel.selectBike(bike.id) },
                        onEdit = {
                            bikeBeingEdited = bike
                            showAddEditDialog = true
                        },
                        onDelete = { bikePendingDelete = bike }
                    )
                }
                item { Spacer(modifier = Modifier.height(64.dp)) }
            }
        }
    }

    if (showAddEditDialog) {
        AddEditBikeDialog(
            existingBike = bikeBeingEdited,
            onDismiss = { showAddEditDialog = false },
            onSave = { name, brand, model, regNumber, odometer ->
                val editing = bikeBeingEdited
                if (editing != null) {
                    viewModel.updateBike(
                        editing.copy(
                            name = name,
                            brand = brand,
                            model = model,
                            registrationNumber = regNumber,
                            initialOdometer = odometer
                        )
                    )
                } else {
                    viewModel.addBike(
                        name = name,
                        brand = brand,
                        model = model,
                        registrationNumber = regNumber,
                        initialOdometer = odometer
                    )
                }
                showAddEditDialog = false
            }
        )
    }

    bikePendingDelete?.let { bike ->
        AlertDialog(
            onDismissRequest = { bikePendingDelete = null },
            title = { Text("Remove ${bike.name}?", fontWeight = FontWeight.Bold) },
            text = { Text("This bike's profile will be removed. Its fuel and maintenance history stays saved but will no longer be shown.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBike(bike)
                        bikePendingDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { bikePendingDelete = null }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun BikeProfileCard(
    bike: Bike,
    isSelected: Boolean,
    canDelete: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.DirectionsBike,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bike.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val subtitle = listOf(bike.brand, bike.model)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                if (bike.registrationNumber.isNotBlank()) {
                    Text(
                        text = bike.registrationNumber,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Active",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            if (canDelete) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditBikeDialog(
    existingBike: Bike?,
    onDismiss: () -> Unit,
    onSave: (name: String, brand: String, model: String, regNumber: String, odometer: Int) -> Unit
) {
    var name by remember { mutableStateOf(existingBike?.name ?: "") }
    var brand by remember { mutableStateOf(existingBike?.brand ?: "") }
    var model by remember { mutableStateOf(existingBike?.model ?: "") }
    var regNumber by remember { mutableStateOf(existingBike?.registrationNumber ?: "") }
    var odometer by remember { mutableStateOf(if (existingBike != null) existingBike.initialOdometer.toString() else "") }
    var nameError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingBike != null) "Edit Bike" else "Add New Bike",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Bike Name*") },
                    placeholder = { Text("e.g. Yamaha R15") },
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = regNumber,
                    onValueChange = { regNumber = it },
                    label = { Text("Registration Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = odometer,
                    onValueChange = { value -> if (value.all { it.isDigit() }) odometer = value },
                    label = { Text("Current Odometer (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank()) {
                    nameError = "Bike name is required"
                } else {
                    onSave(
                        name.trim(),
                        brand.trim(),
                        model.trim(),
                        regNumber.trim(),
                        odometer.toIntOrNull() ?: 0
                    )
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}