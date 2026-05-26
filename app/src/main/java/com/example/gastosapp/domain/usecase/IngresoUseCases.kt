package com.example.gastosapp.domain.usecase

data class IngresoUseCases(
    val obtenerIngresos: ObtenerIngresosUseCase,
    val agregarIngreso: AgregarIngresoUseCase,
    val eliminarIngreso: EliminarIngresoUseCase
)
