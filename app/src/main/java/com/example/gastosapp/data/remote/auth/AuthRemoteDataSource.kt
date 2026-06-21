package com.example.gastosapp.data.remote.auth

interface AuthRemoteDataSource {
    suspend fun iniciarSesion(correo: String, contrasena: String): AuthenticatedFirebaseUser
    fun obtenerUsuarioActual(): AuthenticatedFirebaseUser?
}
