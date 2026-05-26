package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Meta
import com.example.gastosapp.domain.repository.MetaRepository

class AgregarMetaUseCase(
    private val repository: MetaRepository
) {
    @Throws(Exception::class)
    suspend operator fun invoke(meta: Meta) {
        if (meta.monto <= 0) {
            throw Exception("El monto debe ser mayor a cero.")
        }
        if (meta.nombreDeUsuario.isBlank()) {
            throw Exception("El nombre de usuario no puede estar vacío.")
        }
        repository.insertarMeta(meta)
    }
}
