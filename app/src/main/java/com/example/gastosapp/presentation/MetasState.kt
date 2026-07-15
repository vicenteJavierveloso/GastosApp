package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Meta

data class MetaProgress(
    val meta: Meta,
    val montoActual: Int,
    val totalIngresos: Int = 0,
    val totalGastos: Int = 0
)

data class MetasState(
    val metasProgress: List<MetaProgress> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
