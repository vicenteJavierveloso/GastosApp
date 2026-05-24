package com.example.gastosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastosapp.domain.model.Expense
import com.example.gastosapp.domain.usecase.ExpenseUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ExpensesViewModel(
    private val expenseUseCases: ExpenseUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(ExpensesState())
    val state: StateFlow<ExpensesState> = _state.asStateFlow()

    init {
        getExpenses()
    }

    fun onEvent(event: ExpensesEvent) {
        when (event) {
            is ExpensesEvent.AddExpense -> {
                addExpense(event.description, event.amount)
            }
            is ExpensesEvent.DeleteExpense -> {
                deleteExpense(event.expense)
            }
        }
    }

    private fun getExpenses() {
        expenseUseCases.getExpenses().onEach { expenses ->
            _state.value = state.value.copy(
                expenses = expenses
            )
        }.launchIn(viewModelScope)
    }

    private fun addExpense(description: String, amount: Double) {
        viewModelScope.launch {
            try {
                val expense = Expense(
                    id = java.util.UUID.randomUUID().toString(),
                    description = description,
                    amount = amount,
                    date = System.currentTimeMillis()
                )
                expenseUseCases.addExpense(expense)
                _state.value = _state.value.copy(error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error desconocido")
            }
        }
    }

    private fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseUseCases.deleteExpense(expense)
        }
    }
}
