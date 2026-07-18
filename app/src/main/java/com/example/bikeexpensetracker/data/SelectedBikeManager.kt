package com.example.bikeexpensetracker.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * App-wide singleton that keeps track of which bike is currently "active".
 * Every screen/ViewModel that needs to show data for the selected bike reads
 * [selectedBikeId] (a Flow, so switching bikes updates the whole app instantly).
 *
 * Backed by SharedPreferences so the selection survives app restarts.
 */
object SelectedBikeManager {
    private const val PREF_NAME = "bike_prefs"
    private const val KEY_SELECTED_BIKE_ID = "selected_bike_id"
    const val DEFAULT_BIKE_ID = 1

    private var prefs: SharedPreferences? = null

    private val _selectedBikeId = MutableStateFlow(DEFAULT_BIKE_ID)
    val selectedBikeId: StateFlow<Int> = _selectedBikeId.asStateFlow()

    fun init(context: Context) {
        if (prefs != null) return
        val sp = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs = sp
        _selectedBikeId.value = sp.getInt(KEY_SELECTED_BIKE_ID, DEFAULT_BIKE_ID)
    }

    fun selectBike(bikeId: Int) {
        _selectedBikeId.value = bikeId
        prefs?.edit()?.putInt(KEY_SELECTED_BIKE_ID, bikeId)?.apply()
    }

    fun getSelectedBikeId(): Int = _selectedBikeId.value
}