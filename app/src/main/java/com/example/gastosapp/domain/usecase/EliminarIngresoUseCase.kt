package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Ingreso
import com.example.gastosapp.domain.repository.IngresoRepository

class EliminarIngresoUseCase(
    private val repository: IngresoRepository
) {
    suspend operator fun invoke(ingreso: Ingreso) {
        repository.eliminarIngreso(ingreso)
    }
}
