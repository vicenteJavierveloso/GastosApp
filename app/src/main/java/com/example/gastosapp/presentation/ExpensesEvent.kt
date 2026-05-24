package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Expense

sealed class ExpensesEvent {
    data class AddExpense(val description: String, val amount: Double): ExpensesEvent()
    data class DeleteExpense(val expense: Expense): ExpensesEvent()
}
