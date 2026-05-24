package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Expense

data class ExpensesState(
    val expenses: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
