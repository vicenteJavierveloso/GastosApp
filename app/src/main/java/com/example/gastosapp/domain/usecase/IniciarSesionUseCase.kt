package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Usuario
import com.example.gastosapp.domain.repository.AuthRepository

class IniciarSesionUseCase(
    private val repository: AuthRepository
) {
    @Throws(Exception::class)
    suspend operator fun invoke(correo: String, contrasena: String): Usuario {
        if (correo.isBlank()) {
            throw Exception("El correo no puede estar vacío.")
        }
        if (contrasena.isBlank()) {
            throw Exception("La contraseña no puede estar vacía.")
        }
        return repository.iniciarSesion(correo.trim(), contrasena)
    }
}
