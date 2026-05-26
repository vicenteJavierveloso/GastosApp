package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.domain.repository.GastoRepository
import kotlinx.coroutines.flow.Flow

class ObtenerGastosUseCase(
    private val repository: GastoRepository
) {
    operator fun invoke(): Flow<List<Gasto>> {
        return repository.obtenerGastos()
    }
}
