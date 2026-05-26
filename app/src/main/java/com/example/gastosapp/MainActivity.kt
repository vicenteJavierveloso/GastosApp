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
import androidx.room.Room
import com.example.gastosapp.data.local.database.GastosDatabase
import com.example.gastosapp.data.repository.GastoRepositoryImpl
import com.example.gastosapp.domain.usecase.AgregarGastoUseCase
import com.example.gastosapp.domain.usecase.EliminarGastoUseCase
import com.example.gastosapp.domain.usecase.GastoUseCases
import com.example.gastosapp.domain.usecase.ObtenerGastosUseCase
import com.example.gastosapp.presentation.GastosScreen
import com.example.gastosapp.presentation.GastosViewModel
import com.example.gastosapp.presentation.HomeScreen
import com.example.gastosapp.ui.theme.GastosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = Room.databaseBuilder(
            applicationContext,
            GastosDatabase::class.java,
            "gastos.db"
        ).addMigrations(
            GastosDatabase.MIGRATION_2_3,
            GastosDatabase.MIGRATION_3_4
        )
            .fallbackToDestructiveMigration(false)
            .build()

        val repository = GastoRepositoryImpl(
            gastoDao = database.gastoDao(),
            categoriaDao = database.categoriaDao(),
            usuarioDao = database.usuarioDao()
        )
        val useCases = GastoUseCases(
            obtenerGastos = ObtenerGastosUseCase(repository),
            agregarGasto = AgregarGastoUseCase(repository),
            eliminarGasto = EliminarGastoUseCase(repository)
        )
        
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GastosViewModel(useCases) as T
            }
        }
        
        val viewModel = ViewModelProvider(this, viewModelFactory)[GastosViewModel::class.java]

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
                            onNavigateToGastos = {
                                navController.navigate("gastos")
                            }
                        )
                    }
                    composable("gastos") {
                        GastosScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
