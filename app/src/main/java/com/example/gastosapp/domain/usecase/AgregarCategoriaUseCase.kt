package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.repository.CategoriaRepository

class AgregarCategoriaUseCase(
    private val repository: CategoriaRepository
) {
    @Throws(Exception::class)
    suspend operator fun invoke(categoria: Categoria) {
        if (categoria.nombre.isBlank()) {
            throw Exception("El nombre de la categoría no puede estar vacío.")
        }
        if (categoria.nombre.length > 30) {
            throw Exception("El nombre no puede superar los 30 caracteres.")
        }
        repository.insertarCategoria(categoria)
    }
}
