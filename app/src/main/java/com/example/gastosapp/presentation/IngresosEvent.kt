package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Ingreso

sealed class IngresosEvent {
    data class AgregarIngreso(
        val detalle: String,
        val monto: Int,
        val nombreCategoria: String
    ) : IngresosEvent()

    data class EliminarIngreso(val ingreso: Ingreso) : IngresosEvent()
}
