package com.example.bikeexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeexpensetracker.data.BikeExpenseDatabase
import com.example.bikeexpensetracker.model.FuelEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FuelViewModel(application: Application) : AndroidViewModel(application) {
    private val database = BikeExpenseDatabase.getDatabase(application)

    // Get all fuel entries
    val allFuelEntries: Flow<List<FuelEntry>> = database.fuelEntryDao().getAllFuelEntries()

    // Get last fuel entry for mileage calculation
    val lastFuelEntry: Flow<FuelEntry?> = database.fuelEntryDao().getLastFuelEntry()

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