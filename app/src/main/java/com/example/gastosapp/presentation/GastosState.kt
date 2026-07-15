package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.model.Gasto

data class GastosState(
    val gastos: List<Gasto> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
