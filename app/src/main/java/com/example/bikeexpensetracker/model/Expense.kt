package com.example.bikeexpensetracker.model

import java.util.Date
import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val date: Date = Date(),
    val description: String = ""
)

enum class ExpenseCategory {
    FUEL,
    MAINTENANCE,
    INSURANCE,
    ACCESSORIES,
    OTHER
}