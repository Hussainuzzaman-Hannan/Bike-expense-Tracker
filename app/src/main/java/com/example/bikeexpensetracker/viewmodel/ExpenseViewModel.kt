package com.example.bikeexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeexpensetracker.data.BikeExpenseDatabase
import com.example.bikeexpensetracker.model.Expense
import com.example.bikeexpensetracker.model.ExpenseCategory
import com.example.bikeexpensetracker.model.FuelEntry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val database = BikeExpenseDatabase.getDatabase(application)

    // Get all expenses from database (non-fuel expenses from Expense model)
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    // Get all fuel entries from FuelEntry table
    val fuelEntries: Flow<List<FuelEntry>> = database.fuelEntryDao().getAllFuelEntries()

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()

    private val _isAddingExpense = MutableStateFlow(false)
    val isAddingExpense: StateFlow<Boolean> = _isAddingExpense.asStateFlow()

    // Real-time fuel cost - ensure it never emits null
    val totalFuelCost: StateFlow<Double> = database.fuelEntryDao().getTotalFuelCost(1)
        .catch { emit(0.0) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0.0
        )

    // Real-time maintenance cost - get from database
    val totalMaintenanceCost: StateFlow<Double> = database.maintenanceDao().getTotalMaintenanceCost(1)
        .catch { emit(0.0) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0.0
        )

    // Real-time average mileage - ensure it never emits null
    val averageMileage: StateFlow<Double> = fuelEntries
        .map { entries ->
            calculateAverageMileageFromFuelEntries(entries)
        }
        .catch { emit(0.0) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0.0
        )

    init {
        loadExpensesFromDatabase()
    }

    private fun loadExpensesFromDatabase() {
        viewModelScope.launch {
            // For now, using empty list since we're using database
            // You can implement Expense table if needed
            _expenses.value = emptyList()
            calculateTotal()
        }
    }

    fun addExpense(title: String, amount: Double, category: ExpenseCategory, description: String = "") {
        val newExpense = Expense(
            id = UUID.randomUUID().toString(),
            title = title,
            amount = amount,
            category = category,
            date = Date(),
            description = description
        )
        _expenses.value = _expenses.value + newExpense
        calculateTotal()
    }

    fun removeExpense(expenseId: String) {
        _expenses.value = _expenses.value.filter { it.id != expenseId }
        calculateTotal()
    }

    private fun calculateTotal() {
        _totalExpenses.value = _expenses.value.sumOf { it.amount }
    }

    fun toggleAddExpenseDialog() {
        _isAddingExpense.value = !_isAddingExpense.value
    }

    fun getCategoryTotal(category: ExpenseCategory): Double {
        return _expenses.value
            .filter { it.category == category }
            .sumOf { it.amount }
    }

    private fun calculateAverageMileageFromFuelEntries(entries: List<FuelEntry>): Double {
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
}