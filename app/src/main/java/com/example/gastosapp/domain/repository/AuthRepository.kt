package com.example.gastosapp.domain.repository

import com.example.gastosapp.domain.model.Usuario

interface AuthRepository {
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario
}
