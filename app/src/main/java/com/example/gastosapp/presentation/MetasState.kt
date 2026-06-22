package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Meta

data class MetaProgress(
    val meta: Meta,
    val montoActual: Int
)

data class MetasState(
    val metasProgress: List<MetaProgress> = emptyList(),
    val nombreDeUsuario: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
