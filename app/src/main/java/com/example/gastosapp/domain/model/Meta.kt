package com.example.gastosapp.domain.model

import java.util.Date

data class Meta(
    val codigoMeta: Int = 0,
    val monto: Int,
    val nombreDeUsuario: String,
    val nombreCategoria: String,
    val fechaLimite: Date,
    val activa: Boolean = true
)
