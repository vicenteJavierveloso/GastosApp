package com.example.gastosapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastosapp.domain.usecase.IniciarSesionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val iniciarSesionUseCase: IniciarSesionUseCase
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
            AuthEvent.IniciarSesion -> iniciarSesion()
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
}
