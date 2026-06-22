package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Gasto
import java.util.Date

sealed class GastosEvent {
    data class AgregarGasto(
        val detalle: String,
        val monto: Int,
        val nombreCategoria: String,
        val fecha: Date
    ) : GastosEvent()

    data class EliminarGasto(val gasto: Gasto) : GastosEvent()
}
