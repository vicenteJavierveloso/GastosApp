package com.example.gastosapp.domain.usecase

data class GastoUseCases(
    val obtenerGastos: ObtenerGastosUseCase,
    val agregarGasto: AgregarGastoUseCase,
    val eliminarGasto: EliminarGastoUseCase
)
