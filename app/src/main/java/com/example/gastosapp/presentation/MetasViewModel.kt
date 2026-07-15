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
                retirarFondos(event.meta)
            }
            is MetasEvent.DesactivarMeta -> {
                desactivarMeta(event.meta)
            }
            is MetasEvent.ActivarMeta -> {
                activarMeta(event.meta)
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
                        montoActual = totalIngresos - totalGastos,
                        totalIngresos = totalIngresos,
                        totalGastos = totalGastos
                    )
                }
            }.collect { progressList ->
                _state.value = state.value.copy(
                    metasProgress = progressList
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
            _state.value = _state.value.copy(isLoading = true)
            try {
                val username = obtenerUsuarioActualUseCase()?.nombreUsuario ?: ""
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
                _state.value = _state.value.copy(error = null, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error desconocido", isLoading = false)
            }
        }
    }

    private fun eliminarMeta(meta: Meta) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                metaUseCases.eliminarMeta(meta)
                _state.value = _state.value.copy(error = null, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al eliminar meta", isLoading = false)
            }
        }
    }

    private fun aportarFondos(meta: Meta, monto: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
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
                _state.value = _state.value.copy(error = null, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al aportar fondos", isLoading = false)
            }
        }
    }

    private fun retirarFondos(meta: Meta) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Calcular el progreso actual
                val progress = state.value.metasProgress.find { it.meta.codigoMeta == meta.codigoMeta }
                val montoActual = progress?.montoActual ?: 0

                // Restricción: unicamente se permitira sustraer cuando la meta haya sido alcanzada
                if (montoActual < meta.monto) {
                    throw Exception("No se puede completar la meta: la meta de ahorro de $${meta.monto} aún no ha sido alcanzada (ahorro actual: $${montoActual}).")
                }
                if (montoActual <= 0) {
                    throw Exception("No hay fondos acumulados para completar la meta.")
                }

                val gasto = Gasto(
                    monto = montoActual,
                    detalle = "Meta completada: ${meta.nombreCategoria}",
                    nombreDeUsuario = meta.nombreDeUsuario,
                    nombreCategoria = meta.nombreCategoria,
                    fecha = Date()
                )
                agregarGastoUseCase(gasto)
                
                // Desactivar automáticamente al retirar
                val updatedMeta = meta.copy(activa = false)
                metaUseCases.actualizarMeta(updatedMeta)
                
                _state.value = _state.value.copy(error = null, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al completar meta", isLoading = false)
            }
        }
    }

    private fun desactivarMeta(meta: Meta) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val updatedMeta = meta.copy(activa = false)
                metaUseCases.actualizarMeta(updatedMeta)
                _state.value = _state.value.copy(error = null, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al desactivar meta", isLoading = false)
            }
        }
    }

    private fun activarMeta(meta: Meta) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val updatedMeta = meta.copy(activa = true)
                metaUseCases.actualizarMeta(updatedMeta)
                _state.value = _state.value.copy(error = null, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al activar meta", isLoading = false)
            }
        }
    }
}
