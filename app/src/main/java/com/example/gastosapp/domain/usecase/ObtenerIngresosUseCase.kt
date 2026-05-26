package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Ingreso
import com.example.gastosapp.domain.repository.IngresoRepository
import kotlinx.coroutines.flow.Flow

class ObtenerIngresosUseCase(
    private val repository: IngresoRepository
) {
    operator fun invoke(): Flow<List<Ingreso>> {
        return repository.obtenerIngresos()
    }
}
