package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Meta
import com.example.gastosapp.domain.repository.MetaRepository

class ActualizarMetaUseCase(
    private val repository: MetaRepository
) {
    suspend operator fun invoke(meta: Meta) {
        repository.actualizarMeta(meta)
    }
}
