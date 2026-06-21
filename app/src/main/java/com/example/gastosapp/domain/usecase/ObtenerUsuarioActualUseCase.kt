package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Usuario
import com.example.gastosapp.domain.repository.AuthRepository

class ObtenerUsuarioActualUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Usuario? {
        return repository.obtenerUsuarioActual()
    }
}
