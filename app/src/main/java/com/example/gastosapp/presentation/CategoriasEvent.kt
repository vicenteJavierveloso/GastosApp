package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Categoria

sealed class CategoriasEvent {
    data class AgregarCategoria(val nombre: String) : CategoriasEvent()
    data class EliminarCategoria(val categoria: Categoria) : CategoriasEvent()
}
