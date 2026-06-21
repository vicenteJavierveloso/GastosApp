package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.model.Ingreso

data class IngresosState(
    val ingresos: List<Ingreso> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val nombreDeUsuario: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
