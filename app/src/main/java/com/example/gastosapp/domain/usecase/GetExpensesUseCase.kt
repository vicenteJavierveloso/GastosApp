package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Expense
import com.example.gastosapp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow

class GetExpensesUseCase(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<Expense>> {
        return repository.getExpenses()
    }
}
