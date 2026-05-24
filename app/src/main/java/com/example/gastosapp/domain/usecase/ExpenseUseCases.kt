package com.example.gastosapp.domain.usecase

data class ExpenseUseCases(
    val getExpenses: GetExpensesUseCase,
    val addExpense: AddExpenseUseCase,
    val deleteExpense: DeleteExpenseUseCase
)
