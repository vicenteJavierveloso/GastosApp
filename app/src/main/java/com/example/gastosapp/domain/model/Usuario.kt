package com.example.gastosapp.domain.model

import java.util.Date

data class Usuario(
    val nombreUsuario: String,
    val nombre: String,
    val correo: String,
    val ultimoInicioDeSesion: Date,
    val contrasena: String
)
