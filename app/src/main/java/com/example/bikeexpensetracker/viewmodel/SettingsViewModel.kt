package com.example.bikeexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeexpensetracker.data.BikeExpenseDatabase
import com.example.bikeexpensetracker.data.SelectedBikeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val PREF_NAME = "bike_prefs"
        private const val KEY_BIKE_NAME = "bike_name"
        private const val DEFAULT_BIKE_NAME = "Yamaha R15 V4"
    }

    private val prefs = application.getSharedPreferences(PREF_NAME, Application.MODE_PRIVATE)
    private val database = BikeExpenseDatabase.getDatabase(application)

    private val _bikeName = MutableStateFlow(getBikeName())
    val bikeName: StateFlow<String> = _bikeName.asStateFlow()

    private val _isResetting = MutableStateFlow(false)
    val isResetting: StateFlow<Boolean> = _isResetting.asStateFlow()

    private val _resetSuccess = MutableStateFlow(false)
    val resetSuccess: StateFlow<Boolean> = _resetSuccess.asStateFlow()

    fun getBikeName(): String {
        return prefs.getString(KEY_BIKE_NAME, DEFAULT_BIKE_NAME) ?: DEFAULT_BIKE_NAME
    }

    fun saveBikeName(name: String) {
        viewModelScope.launch {
            prefs.edit().putString(KEY_BIKE_NAME, name).apply()
            _bikeName.value = name
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            _isResetting.value = true
            try {
                val bikeId = SelectedBikeManager.getSelectedBikeId()
                // Delete all fuel entries for the selected bike
                database.fuelEntryDao().deleteAllFuelEntries(bikeId)
                // Delete all maintenance entries for the selected bike
                database.maintenanceDao().deleteAllMaintenanceEntries(bikeId)
                _resetSuccess.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isResetting.value = false
                // Reset success message after 2 seconds
                kotlinx.coroutines.delay(2000)
                _resetSuccess.value = false
            }
        }
    }
}