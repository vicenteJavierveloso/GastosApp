package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Meta
import com.example.gastosapp.domain.repository.MetaRepository
import java.util.Date

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
        if (meta.nombreCategoria.isBlank()) {
            throw Exception("El nombre de la categoría no puede estar vacío.")
        }
        if (meta.nombreCategoria.length > 30) {
            throw Exception("El nombre de la categoría no puede superar los 30 caracteres.")
        }
        if (meta.fechaLimite.before(Date())) {
            throw Exception("La fecha límite debe estar en el futuro.")
        }
        repository.insertarMeta(meta)
    }
}
