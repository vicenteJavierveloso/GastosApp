package com.example.gastosapp.data.remote.auth

data class AuthenticatedFirebaseUser(
    val uid: String,
    val correo: String,
    val nombre: String?
)
