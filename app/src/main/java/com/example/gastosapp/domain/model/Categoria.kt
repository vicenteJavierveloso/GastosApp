package com.example.gastosapp.domain.model

enum class TipoCategoria {
    GASTO,
    INGRESO
}

data class Categoria(
    val nombre: String,
    val tipo: TipoCategoria,
    val esDeMeta: Boolean = false
)
