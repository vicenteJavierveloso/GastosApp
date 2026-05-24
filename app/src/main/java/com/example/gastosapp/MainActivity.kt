package com.example.gastosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gastosapp.data.repository.ExpenseRepositoryImpl
import com.example.gastosapp.domain.usecase.AddExpenseUseCase
import com.example.gastosapp.domain.usecase.DeleteExpenseUseCase
import com.example.gastosapp.domain.usecase.ExpenseUseCases
import com.example.gastosapp.domain.usecase.GetExpensesUseCase
import com.example.gastosapp.presentation.ExpensesScreen
import com.example.gastosapp.presentation.ExpensesViewModel
import com.example.gastosapp.presentation.HomeScreen
import com.example.gastosapp.ui.theme.GastosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual Dependency Injection (Placeholder for Hilt/Koin)
        val repository = ExpenseRepositoryImpl()
        val useCases = ExpenseUseCases(
            getExpenses = GetExpensesUseCase(repository),
            addExpense = AddExpenseUseCase(repository),
            deleteExpense = DeleteExpenseUseCase(repository)
        )
        
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExpensesViewModel(useCases) as T
            }
        }
        
        val viewModel = ViewModelProvider(this, viewModelFactory)[ExpensesViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            GastosAppTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            onNavigateToExpenses = {
                                navController.navigate("expenses")
                            }
                        )
                    }
                    composable("expenses") {
                        ExpensesScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
