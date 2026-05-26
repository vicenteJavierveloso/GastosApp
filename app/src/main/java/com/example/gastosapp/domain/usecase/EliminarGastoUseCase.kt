package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.domain.repository.GastoRepository

class EliminarGastoUseCase(
    private val repository: GastoRepository
) {
    suspend operator fun invoke(gasto: Gasto) {
        repository.eliminarGasto(gasto)
    }
}
