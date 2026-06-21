package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Categoria

data class CategoriasState(
    val categorias: List<Categoria> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
