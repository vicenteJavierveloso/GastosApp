package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.repository.CategoriaRepository

class EliminarCategoriaUseCase(
    private val repository: CategoriaRepository
) {
    suspend operator fun invoke(categoria: Categoria) {
        repository.eliminarCategoria(categoria)
    }
}
