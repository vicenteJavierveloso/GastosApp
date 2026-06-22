package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Usuario
import com.example.gastosapp.domain.repository.AuthRepository

class RegistrarUsuarioUseCase(
    private val repository: AuthRepository
) {
    @Throws(Exception::class)
    suspend operator fun invoke(
        nombreUsuario: String,
        nombre: String,
        correo: String,
        contrasena: String
    ): Usuario {
        if (nombreUsuario.isBlank()) {
            throw Exception("El nombre de usuario no puede estar vacío.")
        }
        if (nombreUsuario.length > 30) {
            throw Exception("El nombre de usuario no puede superar los 30 caracteres.")
        }
        if (nombre.isBlank()) {
            throw Exception("El nombre no puede estar vacío.")
        }
        if (correo.isBlank()) {
            throw Exception("El correo no puede estar vacío.")
        }
        if (!correo.contains("@")) {
            throw Exception("El formato del correo no es válido.")
        }
        if (contrasena.isBlank()) {
            throw Exception("La contraseña no puede estar vacía.")
        }
        if (contrasena.length < 8) {
            throw Exception("La contraseña debe tener al menos 8 caracteres.")
        }

        return repository.registrarUsuario(
            nombreUsuario = nombreUsuario.trim(),
            nombre = nombre.trim(),
            correo = correo.trim().lowercase(),
            contrasena = contrasena
        )
    }
}
