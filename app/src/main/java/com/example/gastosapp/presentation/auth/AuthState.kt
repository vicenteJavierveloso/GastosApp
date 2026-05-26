package com.example.gastosapp.presentation.auth

import com.example.gastosapp.domain.model.Usuario

data class AuthState(
    val correo: String = "",
    val contrasena: String = "",
    val usuario: Usuario? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isAuthenticated: Boolean
        get() = usuario != null
}
