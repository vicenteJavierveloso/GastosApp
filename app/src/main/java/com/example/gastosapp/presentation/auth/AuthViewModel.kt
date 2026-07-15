package com.example.gastosapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastosapp.domain.usecase.IniciarSesionUseCase
import com.example.gastosapp.domain.usecase.RegistrarUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val iniciarSesionUseCase: IniciarSesionUseCase,
    private val registrarUsuarioUseCase: RegistrarUsuarioUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.CorreoChanged -> {
                _state.update { it.copy(correo = event.correo, error = null) }
            }
            is AuthEvent.ContrasenaChanged -> {
                _state.update { it.copy(contrasena = event.contrasena, error = null) }
            }
            is AuthEvent.ConfirmarContrasenaChanged -> {
                _state.update { it.copy(confirmarContrasena = event.contrasena, error = null) }
            }
            is AuthEvent.NombreUsuarioChanged -> {
                _state.update { it.copy(nombreUsuario = event.nombreUsuario, error = null) }
            }
            is AuthEvent.NombreChanged -> {
                _state.update { it.copy(nombre = event.nombre, error = null) }
            }
            AuthEvent.IniciarSesion -> iniciarSesion()
            AuthEvent.RegistrarUsuario -> registrarUsuario()
        }
    }

    private fun iniciarSesion() {
        val correo = state.value.correo
        val contrasena = state.value.contrasena

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val usuario = iniciarSesionUseCase(correo, contrasena)
                _state.update {
                    it.copy(
                        usuario = usuario,
                        contrasena = "",
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "No se pudo iniciar sesión."
                    )
                }
            }
        }
    }

    private fun registrarUsuario() {
        val username = state.value.nombreUsuario
        val name = state.value.nombre
        val correo = state.value.correo
        val contrasena = state.value.contrasena
        val confirmarContrasena = state.value.confirmarContrasena

        if (contrasena != confirmarContrasena) {
            _state.update { it.copy(error = "Las contraseñas no coinciden.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val usuario = registrarUsuarioUseCase(username, name, correo, contrasena)
                _state.update {
                    it.copy(
                        usuario = usuario,
                        contrasena = "",
                        confirmarContrasena = "",
                        nombreUsuario = "",
                        nombre = "",
                        isLoading = false,
                        error = null,
                        isRegisteredSuccessfully = true
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "No se pudo registrar el usuario."
                    )
                }
            }
        }
    }
}
