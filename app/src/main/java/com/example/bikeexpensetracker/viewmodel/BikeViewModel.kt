package com.example.bikeexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeexpensetracker.data.BikeExpenseDatabase
import com.example.bikeexpensetracker.data.SelectedBikeManager
import com.example.bikeexpensetracker.model.Bike
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BikeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = BikeExpenseDatabase.getDatabase(application)
    private val bikeDao = database.bikeDao()

    // All bike profiles the user has created
    val bikes: StateFlow<List<Bike>> = bikeDao.getActiveBikes()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Id of whichever bike is currently active app-wide
    val selectedBikeId: StateFlow<Int> = SelectedBikeManager.selectedBikeId

    // The full Bike object for the selected id (falls back to the first bike)
    val selectedBike: StateFlow<Bike?> = combine(bikes, selectedBikeId) { list, id ->
        list.find { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        ensureAtLeastOneBike()
    }

    /** Creates a default "My Bike" profile the very first time the app is opened. */
    private fun ensureAtLeastOneBike() {
        viewModelScope.launch {
            val existing = bikeDao.getActiveBikes().first()
            if (existing.isEmpty()) {
                val newId = bikeDao.insertBike(Bike(name = "My Bike"))
                SelectedBikeManager.selectBike(newId.toInt())
            } else if (existing.none { it.id == SelectedBikeManager.getSelectedBikeId() }) {
                SelectedBikeManager.selectBike(existing.first().id)
            }
        }
    }

    fun selectBike(bikeId: Int) {
        SelectedBikeManager.selectBike(bikeId)
    }

    fun addBike(
        name: String,
        brand: String = "",
        model: String = "",
        registrationNumber: String = "",
        initialOdometer: Int = 0
    ) {
        viewModelScope.launch {
            val newBike = Bike(
                name = name,
                brand = brand,
                model = model,
                registrationNumber = registrationNumber,
                initialOdometer = initialOdometer,
                totalKm = initialOdometer
            )
            val newId = bikeDao.insertBike(newBike)
            // Automatically switch to the newly created bike
            SelectedBikeManager.selectBike(newId.toInt())
        }
    }

    fun updateBike(bike: Bike) {
        viewModelScope.launch {
            bikeDao.updateBike(bike)
        }
    }

    /** Soft-deletes a bike. Refuses to delete the last remaining bike. */
    fun deleteBike(bike: Bike) {
        viewModelScope.launch {
            val current = bikeDao.getActiveBikes().first()
            if (current.size <= 1) return@launch

            bikeDao.updateBike(bike.copy(isActive = false))

            if (SelectedBikeManager.getSelectedBikeId() == bike.id) {
                val remaining = current.filter { it.id != bike.id }
                remaining.firstOrNull()?.let { SelectedBikeManager.selectBike(it.id) }
            }
        }
    }
}