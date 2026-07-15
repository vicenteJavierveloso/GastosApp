package com.example.gastosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.domain.model.TipoCategoria
import com.example.gastosapp.domain.usecase.GastoUseCases
import com.example.gastosapp.domain.usecase.ObtenerCategoriasPorTipoUseCase
import com.example.gastosapp.domain.usecase.ObtenerUsuarioActualUseCase
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GastosViewModel(
    private val gastoUseCases: GastoUseCases,
    private val obtenerCategoriasPorTipoUseCase: ObtenerCategoriasPorTipoUseCase,
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GastosState())
    val state: StateFlow<GastosState> = _state.asStateFlow()

    init {
        obtenerGastos()
        obtenerCategorias()
    }

    fun onEvent(event: GastosEvent) {
        when (event) {
            is GastosEvent.AgregarGasto -> {
                agregarGasto(
                    detalle = event.detalle,
                    monto = event.monto,
                    nombreCategoria = event.nombreCategoria,
                    fecha = event.fecha
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

    private fun obtenerCategorias() {
        obtenerCategoriasPorTipoUseCase(TipoCategoria.GASTO).onEach { categorias ->
            _state.value = state.value.copy(
                categorias = categorias
            )
        }.launchIn(viewModelScope)
    }


    private fun agregarGasto(
        detalle: String,
        monto: Int,
        nombreCategoria: String,
        fecha: Date
    ) {
        viewModelScope.launch {
            try {
                val username = obtenerUsuarioActualUseCase()?.nombreUsuario ?: ""
                if (username.isBlank()) {
                    throw Exception("No hay un usuario autenticado.")
                }
                val gasto = Gasto(
                    monto = monto,
                    detalle = detalle,
                    nombreDeUsuario = username,
                    nombreCategoria = nombreCategoria,
                    fecha = fecha
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
