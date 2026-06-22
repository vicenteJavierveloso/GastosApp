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
import com.example.gastosapp.data.repository.AuthRepositoryImpl
import com.example.gastosapp.data.repository.CategoriaRepositoryImpl
import com.example.gastosapp.data.repository.GastoRepositoryImpl
import com.example.gastosapp.data.repository.IngresoRepositoryImpl
import com.example.gastosapp.data.repository.MetaRepositoryImpl
import com.example.gastosapp.domain.usecase.ActualizarMetaUseCase
import com.example.gastosapp.domain.usecase.AgregarCategoriaUseCase
import com.example.gastosapp.domain.usecase.AgregarGastoUseCase
import com.example.gastosapp.domain.usecase.AgregarIngresoUseCase
import com.example.gastosapp.domain.usecase.AgregarMetaUseCase
import com.example.gastosapp.domain.usecase.CategoriaUseCases
import com.example.gastosapp.domain.usecase.EliminarCategoriaUseCase
import com.example.gastosapp.domain.usecase.EliminarGastoUseCase
import com.example.gastosapp.domain.usecase.EliminarIngresoUseCase
import com.example.gastosapp.domain.usecase.EliminarMetaUseCase
import com.example.gastosapp.domain.usecase.GastoUseCases
import com.example.gastosapp.domain.usecase.IniciarSesionUseCase
import com.example.gastosapp.domain.usecase.RegistrarUsuarioUseCase
import com.example.gastosapp.domain.usecase.IngresoUseCases
import com.example.gastosapp.domain.usecase.MetaUseCases
import com.example.gastosapp.domain.usecase.ObtenerCategoriasUseCase
import com.example.gastosapp.domain.usecase.ObtenerCategoriasPorTipoUseCase
import com.example.gastosapp.domain.usecase.ObtenerGastosUseCase
import com.example.gastosapp.domain.usecase.ObtenerIngresosUseCase
import com.example.gastosapp.domain.usecase.ObtenerMetasUseCase
import com.example.gastosapp.domain.usecase.ObtenerUsuarioActualUseCase
import com.example.gastosapp.presentation.CategoriasScreen
import com.example.gastosapp.presentation.CategoriasViewModel
import com.example.gastosapp.presentation.GastosScreen
import com.example.gastosapp.presentation.GastosViewModel
import com.example.gastosapp.presentation.HomeScreen
import com.example.gastosapp.presentation.HomeViewModel
import com.example.gastosapp.presentation.IngresosScreen
import com.example.gastosapp.presentation.IngresosViewModel
import com.example.gastosapp.presentation.MetasScreen
import com.example.gastosapp.presentation.MetasViewModel
import com.example.gastosapp.presentation.VerGastosScreen
import com.example.gastosapp.presentation.VerIngresosScreen
import com.example.gastosapp.presentation.auth.AuthViewModel
import com.example.gastosapp.presentation.auth.LoginScreen
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
            GastosDatabase.MIGRATION_3_4,
            GastosDatabase.MIGRATION_4_5
        )
            .fallbackToDestructiveMigration(true)
            .build()

        com.example.gastosapp.data.remote.BackendClient.init(applicationContext)

        val repository = GastoRepositoryImpl(
            gastoDao = database.gastoDao(),
            categoriaDao = database.categoriaDao(),
            usuarioDao = database.usuarioDao()
        )
        val ingresoRepository = IngresoRepositoryImpl(
            ingresoDao = database.ingresoDao(),
            categoriaDao = database.categoriaDao(),
            usuarioDao = database.usuarioDao()
        )
        val categoriaRepository = CategoriaRepositoryImpl(
            categoriaDao = database.categoriaDao()
        )
        val metaRepository = MetaRepositoryImpl(
            metaDao = database.metaDao(),
            usuarioDao = database.usuarioDao(),
            categoriaDao = database.categoriaDao()
        )

        val useCases = GastoUseCases(
            obtenerGastos = ObtenerGastosUseCase(repository),
            agregarGasto = AgregarGastoUseCase(repository),
            eliminarGasto = EliminarGastoUseCase(repository)
        )
        val ingresoUseCases = IngresoUseCases(
            obtenerIngresos = ObtenerIngresosUseCase(ingresoRepository),
            agregarIngreso = AgregarIngresoUseCase(ingresoRepository),
            eliminarIngreso = EliminarIngresoUseCase(ingresoRepository)
        )
        val categoriaUseCases = CategoriaUseCases(
            obtenerCategorias = ObtenerCategoriasUseCase(categoriaRepository),
            obtenerCategoriasPorTipo = ObtenerCategoriasPorTipoUseCase(categoriaRepository),
            agregarCategoria = AgregarCategoriaUseCase(categoriaRepository),
            eliminarCategoria = EliminarCategoriaUseCase(categoriaRepository)
        )
        val metaUseCases = MetaUseCases(
            obtenerMetas = ObtenerMetasUseCase(metaRepository),
            agregarMeta = AgregarMetaUseCase(metaRepository),
            eliminarMeta = EliminarMetaUseCase(metaRepository),
            actualizarMeta = ActualizarMetaUseCase(metaRepository)
        )

        val authRepository = AuthRepositoryImpl(
            database = database
        )
        val iniciarSesionUseCase = IniciarSesionUseCase(authRepository)
        val registrarUsuarioUseCase = RegistrarUsuarioUseCase(authRepository)
        val obtenerUsuarioActualUseCase = ObtenerUsuarioActualUseCase(authRepository)
        
        val gastosViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GastosViewModel(
                    gastoUseCases = useCases,
                    obtenerCategoriasPorTipoUseCase = categoriaUseCases.obtenerCategoriasPorTipo,
                    obtenerUsuarioActualUseCase = obtenerUsuarioActualUseCase
                ) as T
            }
        }
        val homeViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    obtenerGastos = useCases.obtenerGastos,
                    obtenerIngresos = ingresoUseCases.obtenerIngresos
                ) as T
            }
        }
        val authViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(iniciarSesionUseCase, registrarUsuarioUseCase) as T
            }
        }
        val categoriasViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoriasViewModel(categoriaUseCases) as T
            }
        }
        val ingresosViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return IngresosViewModel(
                    ingresoUseCases = ingresoUseCases,
                    obtenerCategoriasPorTipoUseCase = categoriaUseCases.obtenerCategoriasPorTipo,
                    obtenerUsuarioActualUseCase = obtenerUsuarioActualUseCase
                ) as T
            }
        }
        val metasViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MetasViewModel(
                    metaUseCases = metaUseCases,
                    obtenerIngresosUseCase = ingresoUseCases.obtenerIngresos,
                    obtenerGastosUseCase = useCases.obtenerGastos,
                    agregarIngresoUseCase = ingresoUseCases.agregarIngreso,
                    agregarGastoUseCase = useCases.agregarGasto,
                    obtenerUsuarioActualUseCase = obtenerUsuarioActualUseCase
                ) as T
            }
        }
        
        val gastosViewModel = ViewModelProvider(
            this,
            gastosViewModelFactory
        )[GastosViewModel::class.java]
        val homeViewModel = ViewModelProvider(
            this,
            homeViewModelFactory
        )[HomeViewModel::class.java]
        val authViewModel = ViewModelProvider(
            this,
            authViewModelFactory
        )[AuthViewModel::class.java]
        val categoriasViewModel = ViewModelProvider(
            this,
            categoriasViewModelFactory
        )[CategoriasViewModel::class.java]
        val ingresosViewModel = ViewModelProvider(
            this,
            ingresosViewModelFactory
        )[IngresosViewModel::class.java]
        val metasViewModel = ViewModelProvider(
            this,
            metasViewModelFactory
        )[MetasViewModel::class.java]

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
                            viewModel = homeViewModel,
                            onNavigateToGastos = {
                                navController.navigate("gastos")
                            },
                            onNavigateToIngresos = {
                                navController.navigate("ingresos")
                            },
                            onNavigateToCategorias = {
                                navController.navigate("categorias")
                            },
                            onNavigateToMetas = {
                                navController.navigate("metas")
                            },
                            onNavigateToVerIngresos = {
                                navController.navigate("ver_ingresos")
                            },
                            onNavigateToVerGastos = {
                                navController.navigate("ver_gastos")
                            }
                        )
                    }
                    composable("gastos") {
                        GastosScreen(
                            viewModel = gastosViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("ingresos") {
                        IngresosScreen(
                            viewModel = ingresosViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("categorias") {
                        CategoriasScreen(
                            viewModel = categoriasViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("metas") {
                        MetasScreen(
                            viewModel = metasViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("ver_ingresos") {
                        VerIngresosScreen(
                            viewModel = ingresosViewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToAdd = { navController.navigate("ingresos") }
                        )
                    }
                    composable("ver_gastos") {
                        VerGastosScreen(
                            viewModel = gastosViewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToAdd = { navController.navigate("gastos") }
                        )
                    }
                }
            }
        }
    }
}
