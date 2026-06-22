package com.example.gastosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.domain.model.Ingreso
import com.example.gastosapp.domain.model.Meta
import com.example.gastosapp.domain.usecase.AgregarGastoUseCase
import com.example.gastosapp.domain.usecase.AgregarIngresoUseCase
import com.example.gastosapp.domain.usecase.MetaUseCases
import com.example.gastosapp.domain.usecase.ObtenerGastosUseCase
import com.example.gastosapp.domain.usecase.ObtenerIngresosUseCase
import com.example.gastosapp.domain.usecase.ObtenerUsuarioActualUseCase
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MetasViewModel(
    private val metaUseCases: MetaUseCases,
    private val obtenerIngresosUseCase: ObtenerIngresosUseCase,
    private val obtenerGastosUseCase: ObtenerGastosUseCase,
    private val agregarIngresoUseCase: AgregarIngresoUseCase,
    private val agregarGastoUseCase: AgregarGastoUseCase,
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MetasState())
    val state: StateFlow<MetasState> = _state.asStateFlow()

    init {
        observeMetas()
        cargarUsuarioActual()
    }

    fun onEvent(event: MetasEvent) {
        when (event) {
            is MetasEvent.AgregarMeta -> {
                agregarMeta(
                    monto = event.monto,
                    nombreCategoria = event.nombreCategoria,
                    fechaLimite = event.fechaLimite
                )
            }
            is MetasEvent.EliminarMeta -> {
                eliminarMeta(event.meta)
            }
            is MetasEvent.AportarFondos -> {
                aportarFondos(event.meta, event.monto)
            }
            is MetasEvent.RetirarFondos -> {
                retirarFondos(event.meta, event.monto)
            }
        }
    }

    private fun observeMetas() {
        viewModelScope.launch {
            combine(
                metaUseCases.obtenerMetas(),
                obtenerIngresosUseCase(),
                obtenerGastosUseCase()
            ) { metas, ingresos, gastos ->
                metas.map { meta ->
                    val totalIngresos = ingresos
                        .filter { it.nombreCategoria == meta.nombreCategoria && it.nombreDeUsuario == meta.nombreDeUsuario }
                        .sumOf { it.monto }
                    val totalGastos = gastos
                        .filter { it.nombreCategoria == meta.nombreCategoria && it.nombreDeUsuario == meta.nombreDeUsuario }
                        .sumOf { it.monto }

                    MetaProgress(
                        meta = meta,
                        montoActual = totalIngresos - totalGastos
                    )
                }
            }.collect { progressList ->
                _state.value = state.value.copy(
                    metasProgress = progressList
                )
            }
        }
    }

    private fun cargarUsuarioActual() {
        viewModelScope.launch {
            try {
                val usuario = obtenerUsuarioActualUseCase()
                if (usuario != null) {
                    _state.value = state.value.copy(
                        nombreDeUsuario = usuario.nombreUsuario
                    )
                }
            } catch (e: Exception) {
                _state.value = state.value.copy(
                    error = e.message ?: "Error al cargar usuario actual"
                )
            }
        }
    }

    private fun agregarMeta(
        monto: Int,
        nombreCategoria: String,
        fechaLimite: Date
    ) {
        viewModelScope.launch {
            try {
                val username = state.value.nombreDeUsuario
                if (username.isBlank()) {
                    throw Exception("No hay un usuario autenticado.")
                }
                val meta = Meta(
                    monto = monto,
                    nombreDeUsuario = username,
                    nombreCategoria = nombreCategoria,
                    fechaLimite = fechaLimite
                )
                metaUseCases.agregarMeta(meta)
                _state.value = _state.value.copy(error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error desconocido")
            }
        }
    }

    private fun eliminarMeta(meta: Meta) {
        viewModelScope.launch {
            try {
                metaUseCases.eliminarMeta(meta)
                _state.value = _state.value.copy(error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al eliminar meta")
            }
        }
    }

    private fun aportarFondos(meta: Meta, monto: Int) {
        viewModelScope.launch {
            try {
                if (monto <= 0) {
                    throw Exception("El monto a aportar debe ser mayor a cero.")
                }
                val ingreso = Ingreso(
                    monto = monto,
                    detalle = "Aporte a meta: ${meta.nombreCategoria}",
                    nombreDeUsuario = meta.nombreDeUsuario,
                    nombreCategoria = meta.nombreCategoria,
                    fecha = Date()
                )
                agregarIngresoUseCase(ingreso)
                _state.value = _state.value.copy(error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al aportar fondos")
            }
        }
    }

    private fun retirarFondos(meta: Meta, monto: Int) {
        viewModelScope.launch {
            try {
                if (monto <= 0) {
                    throw Exception("El monto a retirar debe ser mayor a cero.")
                }

                // Calcular el progreso actual
                val progress = state.value.metasProgress.find { it.meta.codigoMeta == meta.codigoMeta }
                val montoActual = progress?.montoActual ?: 0

                // Restricción: unicamente se permitira sustraer cuando la meta haya sido alcanzada
                if (montoActual < meta.monto) {
                    throw Exception("No se puede retirar fondos: la meta de ahorro de $${meta.monto} aún no ha sido alcanzada (ahorro actual: $${montoActual}).")
                }
                if (monto > montoActual) {
                    throw Exception("No puede retirar más del monto actual acumulado ($${montoActual}).")
                }

                val gasto = Gasto(
                    monto = monto,
                    detalle = "Retiro de meta: ${meta.nombreCategoria}",
                    nombreDeUsuario = meta.nombreDeUsuario,
                    nombreCategoria = meta.nombreCategoria,
                    fecha = Date()
                )
                agregarGastoUseCase(gasto)
                _state.value = _state.value.copy(error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al retirar fondos")
            }
        }
    }
}
