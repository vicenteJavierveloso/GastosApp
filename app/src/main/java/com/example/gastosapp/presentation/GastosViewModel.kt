package com.example.gastosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.domain.usecase.GastoUseCases
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GastosViewModel(
    private val gastoUseCases: GastoUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(GastosState())
    val state: StateFlow<GastosState> = _state.asStateFlow()

    init {
        obtenerGastos()
    }

    fun onEvent(event: GastosEvent) {
        when (event) {
            is GastosEvent.AgregarGasto -> {
                agregarGasto(
                    detalle = event.detalle,
                    monto = event.monto,
                    nombreDeUsuario = event.nombreDeUsuario,
                    nombreCategoria = event.nombreCategoria
                )
            }
            is GastosEvent.EliminarGasto -> {
                eliminarGasto(event.gasto)
            }
        }
    }

    private fun obtenerGastos() {
        gastoUseCases.obtenerGastos().onEach { gastos ->
            _state.value = state.value.copy(
                gastos = gastos
            )
        }.launchIn(viewModelScope)
    }

    private fun agregarGasto(
        detalle: String,
        monto: Int,
        nombreDeUsuario: String,
        nombreCategoria: String
    ) {
        viewModelScope.launch {
            try {
                val gasto = Gasto(
                    monto = monto,
                    detalle = detalle,
                    nombreDeUsuario = nombreDeUsuario,
                    nombreCategoria = nombreCategoria,
                    fecha = Date()
                )
                gastoUseCases.agregarGasto(gasto)
                _state.value = _state.value.copy(error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error desconocido")
            }
        }
    }

    private fun eliminarGasto(gasto: Gasto) {
        viewModelScope.launch {
            gastoUseCases.eliminarGasto(gasto)
        }
    }
}
