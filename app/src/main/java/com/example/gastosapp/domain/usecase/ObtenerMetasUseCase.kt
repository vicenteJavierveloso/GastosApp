package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Meta
import com.example.gastosapp.domain.repository.MetaRepository
import kotlinx.coroutines.flow.Flow

class ObtenerMetasUseCase(
    private val repository: MetaRepository
) {
    operator fun invoke(): Flow<List<Meta>> {
        return repository.obtenerMetas()
    }
}
