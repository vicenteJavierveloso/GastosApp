package com.example.gastosapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gastosapp.domain.model.Expense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Gastos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<") // Placeholder para icono de volver
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Monto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    viewModel.onEvent(ExpensesEvent.AddExpense(description, amountDouble))
                    if (state.error == null) {
                        description = ""
                        amount = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar Gasto")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(state.expenses) { expense ->
                    ExpenseItem(
                        expense = expense,
                        onDelete = { viewModel.onEvent(ExpensesEvent.DeleteExpense(expense)) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = expense.description, style = MaterialTheme.typography.titleMedium)
                Text(text = "$${expense.amount}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Text("X")
            }
        }
    }
}
