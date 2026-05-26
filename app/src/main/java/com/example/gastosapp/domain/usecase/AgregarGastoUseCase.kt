package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.domain.repository.GastoRepository

class AgregarGastoUseCase(
    private val repository: GastoRepository
) {
    @Throws(Exception::class)
    suspend operator fun invoke(gasto: Gasto) {
        if (gasto.detalle.isBlank()) {
            throw Exception("El detalle no puede estar vacío.")
        }
        if (gasto.monto <= 0) {
            throw Exception("El monto debe ser mayor a cero.")
        }
        if (gasto.nombreDeUsuario.isBlank()) {
            throw Exception("El nombre de usuario no puede estar vacío.")
        }
        if (gasto.nombreCategoria.isBlank()) {
            throw Exception("La categoría no puede estar vacía.")
        }
        repository.insertarGasto(gasto)
    }
}
