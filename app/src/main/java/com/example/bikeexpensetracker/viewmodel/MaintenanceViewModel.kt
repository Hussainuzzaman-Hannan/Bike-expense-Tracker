package com.example.bikeexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeexpensetracker.data.BikeExpenseDatabase
import com.example.bikeexpensetracker.data.SelectedBikeManager
import com.example.bikeexpensetracker.model.MaintenanceCategory
import com.example.bikeexpensetracker.model.MaintenanceEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Date

class MaintenanceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = BikeExpenseDatabase.getDatabase(application)

    val allMaintenanceEntries: Flow<List<MaintenanceEntry>> = SelectedBikeManager.selectedBikeId.flatMapLatest { bikeId ->
        database.maintenanceDao().getMaintenanceEntriesByBike(bikeId)
    }

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun addMaintenanceEntry(entry: MaintenanceEntry) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                database.maintenanceDao().insertMaintenanceEntry(entry)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteMaintenanceEntry(entry: MaintenanceEntry) {
        viewModelScope.launch {
            database.maintenanceDao().deleteMaintenanceEntry(entry)
        }
    }

    fun updateMaintenanceEntry(entry: MaintenanceEntry) {
        viewModelScope.launch {
            database.maintenanceDao().updateMaintenanceEntry(entry)
        }
    }

    fun getTotalMaintenanceCost(bikeId: Int): Flow<Double> {
        return database.maintenanceDao().getTotalMaintenanceCost(bikeId)
    }
}