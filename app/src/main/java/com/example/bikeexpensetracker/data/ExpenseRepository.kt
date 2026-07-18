package com.example.bikeexpensetracker.data

import com.example.bikeexpensetracker.model.Expense
import com.example.bikeexpensetracker.model.ExpenseCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class ExpenseRepository {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    init {
        // Add some sample data
        addSampleData()
    }

    fun addExpense(expense: Expense) {
        _expenses.value = _expenses.value + expense
    }

    fun removeExpense(expenseId: String) {
        _expenses.value = _expenses.value.filter { it.id != expenseId }
    }

    fun getTotalExpenses(): Double {
        return _expenses.value.sumOf { it.amount }
    }

    fun getExpensesByCategory(category: ExpenseCategory): List<Expense> {
        return _expenses.value.filter { it.category == category }
    }

    fun getCategoryTotal(category: ExpenseCategory): Double {
        return getExpensesByCategory(category).sumOf { it.amount }
    }

    private fun addSampleData() {
        val sampleExpenses = listOf(
            Expense(
                title = "Petrol",
                amount = 500.0,
                category = ExpenseCategory.FUEL,
                date = Date()
            ),
            Expense(
                title = "Oil Change",
                amount = 800.0,
                category = ExpenseCategory.MAINTENANCE,
                date = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
            ),
            Expense(
                title = "Helmet",
                amount = 2500.0,
                category = ExpenseCategory.ACCESSORIES,
                date = Date(System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000)
            )
        )
        _expenses.value = sampleExpenses
    }
}