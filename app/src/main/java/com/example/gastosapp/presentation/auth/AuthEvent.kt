package com.example.gastosapp.presentation.auth

sealed class AuthEvent {
    data class CorreoChanged(val correo: String) : AuthEvent()
    data class ContrasenaChanged(val contrasena: String) : AuthEvent()
    data object IniciarSesion : AuthEvent()
}
