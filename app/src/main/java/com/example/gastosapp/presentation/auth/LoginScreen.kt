package com.example.gastosapp.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.example.gastosapp.presentation.components.LoadingModal
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var isRegisterMode by remember { mutableStateOf(false) }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onLoginSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (isRegisterMode) "Crear Cuenta" else "Iniciar Sesión",
                        fontWeight = FontWeight.Bold
                    ) 
                }
            )
        }
    ) { padding ->
        LoadingModal(isLoading = state.isLoading)
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRegisterMode) "Únete a GastosApp" else "Bienvenido de nuevo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (isRegisterMode) "Regístrate para comenzar a gestionar tus finanzas" else "Ingresa tus datos para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (isRegisterMode) {
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = { viewModel.onEvent(AuthEvent.NombreChanged(it)) },
                    label = { Text("Nombre Completo") },
                    enabled = !state.isLoading,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.nombreUsuario,
                    onValueChange = { viewModel.onEvent(AuthEvent.NombreUsuarioChanged(it)) },
                    label = { Text("Nombre de Usuario") },
                    enabled = !state.isLoading,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = state.correo,
                onValueChange = { viewModel.onEvent(AuthEvent.CorreoChanged(it)) },
                label = { Text("Correo Electrónico") },
                enabled = !state.isLoading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.contrasena,
                onValueChange = { viewModel.onEvent(AuthEvent.ContrasenaChanged(it)) },
                label = { Text("Contraseña") },
                enabled = !state.isLoading,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            if (isRegisterMode) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.confirmarContrasena,
                    onValueChange = { viewModel.onEvent(AuthEvent.ConfirmarContrasenaChanged(it)) },
                    label = { Text("Confirmar Contraseña") },
                    enabled = !state.isLoading,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { 
                    if (isRegisterMode) {
                        viewModel.onEvent(AuthEvent.RegistrarUsuario)
                    } else {
                        viewModel.onEvent(AuthEvent.IniciarSesion)
                    }
                },
                enabled = !state.isLoading,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (isRegisterMode) "Registrarse" else "Ingresar",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { 
                    isRegisterMode = !isRegisterMode
                },
                enabled = !state.isLoading
            ) {
                Text(
                    text = if (isRegisterMode) "¿Ya tienes una cuenta? Inicia Sesión" else "¿No tienes una cuenta? Regístrate aquí",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
