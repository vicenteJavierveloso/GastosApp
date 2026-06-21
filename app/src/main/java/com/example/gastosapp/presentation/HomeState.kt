package com.example.gastosapp.presentation

data class HomeState(
    val totalGastos: Int = 0,
    val totalIngresos: Int = 0,
    val balance: Int = 0,
    val cantidadGastos: Int = 0,
    val cantidadIngresos: Int = 0,
    val principalCategoriaGasto: String? = null
)
