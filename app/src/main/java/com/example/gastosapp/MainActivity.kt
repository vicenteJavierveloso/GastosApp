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
import com.example.gastosapp.data.remote.auth.FirebaseAuthRemoteDataSource
import com.example.gastosapp.data.repository.AuthRepositoryImpl
import com.example.gastosapp.data.repository.GastoRepositoryImpl
import com.example.gastosapp.domain.usecase.AgregarGastoUseCase
import com.example.gastosapp.domain.usecase.EliminarGastoUseCase
import com.example.gastosapp.domain.usecase.GastoUseCases
import com.example.gastosapp.domain.usecase.IniciarSesionUseCase
import com.example.gastosapp.domain.usecase.ObtenerGastosUseCase
import com.example.gastosapp.presentation.GastosScreen
import com.example.gastosapp.presentation.GastosViewModel
import com.example.gastosapp.presentation.HomeScreen
import com.example.gastosapp.presentation.auth.AuthViewModel
import com.example.gastosapp.presentation.auth.LoginScreen
import com.example.gastosapp.ui.theme.GastosAppTheme
import com.google.firebase.auth.FirebaseAuth

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
        val authRepository = AuthRepositoryImpl(
            authRemoteDataSource = FirebaseAuthRemoteDataSource(FirebaseAuth.getInstance()),
            usuarioDao = database.usuarioDao()
        )
        val iniciarSesionUseCase = IniciarSesionUseCase(authRepository)
        
        val gastosViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GastosViewModel(useCases) as T
            }
        }
        val authViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(iniciarSesionUseCase) as T
            }
        }
        
        val gastosViewModel = ViewModelProvider(
            this,
            gastosViewModelFactory
        )[GastosViewModel::class.java]
        val authViewModel = ViewModelProvider(
            this,
            authViewModelFactory
        )[AuthViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            GastosAppTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            viewModel = authViewModel,
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onNavigateToGastos = {
                                navController.navigate("gastos")
                            }
                        )
                    }
                    composable("gastos") {
                        GastosScreen(
                            viewModel = gastosViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
