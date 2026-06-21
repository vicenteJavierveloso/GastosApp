package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.model.TipoCategoria

sealed class CategoriasEvent {
    data class AgregarCategoria(val nombre: String, val tipo: TipoCategoria) : CategoriasEvent()
    data class EliminarCategoria(val categoria: Categoria) : CategoriasEvent()
}
