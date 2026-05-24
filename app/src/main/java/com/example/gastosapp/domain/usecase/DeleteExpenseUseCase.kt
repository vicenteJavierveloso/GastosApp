package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Expense
import com.example.gastosapp.domain.repository.ExpenseRepository

class DeleteExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        repository.deleteExpense(expense)
    }
}
