package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Expense
import com.example.gastosapp.domain.repository.ExpenseRepository

class AddExpenseUseCase(
    private val repository: ExpenseRepository
) {
    @Throws(Exception::class)
    suspend operator fun invoke(expense: Expense) {
        if (expense.description.isBlank()) {
            throw Exception("La descripción no puede estar vacía.")
        }
        if (expense.amount <= 0) {
            throw Exception("El monto debe ser mayor a cero.")
        }
        repository.insertExpense(expense)
    }
}
