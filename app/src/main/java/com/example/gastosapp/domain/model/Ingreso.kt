package com.example.gastosapp.domain.model

import java.util.Date

data class Ingreso(
    val codigoIngreso: Int = 0,
    val monto: Int,
    val detalle: String,
    val nombreDeUsuario: String,
    val nombreCategoria: String,
    val fecha: Date
)
