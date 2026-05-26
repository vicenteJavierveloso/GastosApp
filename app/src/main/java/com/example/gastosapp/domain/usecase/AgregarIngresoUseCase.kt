package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Ingreso
import com.example.gastosapp.domain.repository.IngresoRepository

class AgregarIngresoUseCase(
    private val repository: IngresoRepository
) {
    @Throws(Exception::class)
    suspend operator fun invoke(ingreso: Ingreso) {
        if (ingreso.detalle.isBlank()) {
            throw Exception("El detalle no puede estar vacío.")
        }
        if (ingreso.monto <= 0) {
            throw Exception("El monto debe ser mayor a cero.")
        }
        if (ingreso.nombreDeUsuario.isBlank()) {
            throw Exception("El nombre de usuario no puede estar vacío.")
        }
        if (ingreso.nombreCategoria.isBlank()) {
            throw Exception("La categoría no puede estar vacía.")
        }
        repository.insertarIngreso(ingreso)
    }
}
