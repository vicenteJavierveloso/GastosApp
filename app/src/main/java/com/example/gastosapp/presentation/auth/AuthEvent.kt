package com.example.gastosapp.presentation.auth

sealed class AuthEvent {
    data class CorreoChanged(val correo: String) : AuthEvent()
    data class ContrasenaChanged(val contrasena: String) : AuthEvent()
    data class NombreUsuarioChanged(val nombreUsuario: String) : AuthEvent()
    data class NombreChanged(val nombre: String) : AuthEvent()
    data class ConfirmarContrasenaChanged(val contrasena: String) : AuthEvent()
    data object IniciarSesion : AuthEvent()
    data object RegistrarUsuario : AuthEvent()
}
