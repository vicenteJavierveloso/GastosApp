package com.example.gastosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastosapp.domain.model.Ingreso
import com.example.gastosapp.domain.model.TipoCategoria
import com.example.gastosapp.domain.usecase.IngresoUseCases
import com.example.gastosapp.domain.usecase.ObtenerCategoriasPorTipoUseCase
import com.example.gastosapp.domain.usecase.ObtenerUsuarioActualUseCase
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class IngresosViewModel(
    private val ingresoUseCases: IngresoUseCases,
    private val obtenerCategoriasPorTipoUseCase: ObtenerCategoriasPorTipoUseCase,
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(IngresosState())
    val state: StateFlow<IngresosState> = _state.asStateFlow()

    init {
        obtenerIngresos()
        obtenerCategorias()
    }

    fun onEvent(event: IngresosEvent) {
        when (event) {
            is IngresosEvent.AgregarIngreso -> {
                agregarIngreso(
                    detalle = event.detalle,
                    monto = event.monto,
                    nombreCategoria = event.nombreCategoria,
                    fecha = event.fecha
                )
            }
            is IngresosEvent.EliminarIngreso -> {
                eliminarIngreso(event.ingreso)
            }
        }
    }

    private fun obtenerIngresos() {
        ingresoUseCases.obtenerIngresos().onEach { ingresos ->
            _state.value = state.value.copy(
                ingresos = ingresos
            )
        }.launchIn(viewModelScope)
    }

    private fun obtenerCategorias() {
        obtenerCategoriasPorTipoUseCase(TipoCategoria.INGRESO).onEach { categorias ->
            _state.value = state.value.copy(
                categorias = categorias
            )
        }.launchIn(viewModelScope)
    }


    private fun agregarIngreso(
        detalle: String,
        monto: Int,
        nombreCategoria: String,
        fecha: Date
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val username = obtenerUsuarioActualUseCase()?.nombreUsuario ?: ""
                if (username.isBlank()) {
                    throw Exception("No hay un usuario autenticado.")
                }
                val ingreso = Ingreso(
                    monto = monto,
                    detalle = detalle,
                    nombreDeUsuario = username,
                    nombreCategoria = nombreCategoria,
                    fecha = fecha
                )
                ingresoUseCases.agregarIngreso(ingreso)
                _state.value = _state.value.copy(error = null, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error desconocido", isLoading = false)
            }
        }
    }

    private fun eliminarIngreso(ingreso: Ingreso) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                ingresoUseCases.eliminarIngreso(ingreso)
                _state.value = _state.value.copy(error = null, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al eliminar ingreso", isLoading = false)
            }
        }
    }
}
