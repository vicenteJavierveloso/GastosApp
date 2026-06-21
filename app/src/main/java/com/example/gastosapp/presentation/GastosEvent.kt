package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Gasto

sealed class GastosEvent {
    data class AgregarGasto(
        val detalle: String,
        val monto: Int,
        val nombreCategoria: String
    ) : GastosEvent()

    data class EliminarGasto(val gasto: Gasto) : GastosEvent()
}
