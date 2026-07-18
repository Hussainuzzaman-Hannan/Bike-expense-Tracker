package com.example.bikeexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeexpensetracker.data.BikeExpenseDatabase
import com.example.bikeexpensetracker.data.SelectedBikeManager
import com.example.bikeexpensetracker.model.FuelEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FuelViewModel(application: Application) : AndroidViewModel(application) {
    private val database = BikeExpenseDatabase.getDatabase(application)

    // Get fuel entries for the currently selected bike
    val allFuelEntries: Flow<List<FuelEntry>> = SelectedBikeManager.selectedBikeId.flatMapLatest { bikeId ->
        database.fuelEntryDao().getFuelEntriesByBike(bikeId)
    }

    // Get last fuel entry (for the selected bike) for mileage calculation
    val lastFuelEntry: Flow<FuelEntry?> = allFuelEntries.map { it.firstOrNull() }

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun addFuelEntry(entry: FuelEntry) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                database.fuelEntryDao().insertFuelEntry(entry)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteFuelEntry(entry: FuelEntry) {
        viewModelScope.launch {
            database.fuelEntryDao().deleteFuelEntry(entry)
        }
    }
}