package com.example.gastosapp.data.repository

import com.example.gastosapp.domain.model.Expense
import com.example.gastosapp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExpenseRepositoryImpl : ExpenseRepository {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    
    override fun getExpenses(): Flow<List<Expense>> {
        return _expenses.asStateFlow()
    }

    override suspend fun insertExpense(expense: Expense) {
        _expenses.update { currentList ->
            currentList + expense
        }
    }

    override suspend fun deleteExpense(expense: Expense) {
        _expenses.update { currentList ->
            currentList.filter { it.id != expense.id }
        }
    }
}
