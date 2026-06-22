package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Ingreso
import java.util.Date

sealed class IngresosEvent {
    data class AgregarIngreso(
        val detalle: String,
        val monto: Int,
        val nombreCategoria: String,
        val fecha: Date
    ) : IngresosEvent()

    data class EliminarIngreso(val ingreso: Ingreso) : IngresosEvent()
}
