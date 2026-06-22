package com.example.gastosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.domain.usecase.ObtenerGastosUseCase
import com.example.gastosapp.domain.usecase.ObtenerIngresosUseCase
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val obtenerGastos: ObtenerGastosUseCase,
    private val obtenerIngresos: ObtenerIngresosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeStats()
    }

    private fun observeStats() {
        viewModelScope.launch {
            combine(
                obtenerGastos(),
                obtenerIngresos()
            ) { gastos, ingresos ->
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH)

                val currentMonthGastos = gastos.filter { gasto ->
                    calendar.time = gasto.fecha
                    calendar.get(Calendar.YEAR) == currentYear && calendar.get(Calendar.MONTH) == currentMonth
                }

                val currentMonthIngresos = ingresos.filter { ingreso ->
                    calendar.time = ingreso.fecha
                    calendar.get(Calendar.YEAR) == currentYear && calendar.get(Calendar.MONTH) == currentMonth
                }

                val totalGastos = currentMonthGastos.sumOf { it.monto }
                val totalIngresos = currentMonthIngresos.sumOf { it.monto }

                HomeState(
                    totalGastos = totalGastos,
                    totalIngresos = totalIngresos,
                    balance = totalIngresos - totalGastos,
                    cantidadGastos = currentMonthGastos.size,
                    cantidadIngresos = currentMonthIngresos.size,
                    principalCategoriaGasto = currentMonthGastos.principalCategoria()
                )
            }.collect { nextState ->
                _state.value = nextState
            }
        }
    }

    private fun List<Gasto>.principalCategoria(): String? =
        groupingBy { it.nombreCategoria }
            .fold(0) { total, gasto -> total + gasto.monto }
            .maxByOrNull { it.value }
            ?.key
}
