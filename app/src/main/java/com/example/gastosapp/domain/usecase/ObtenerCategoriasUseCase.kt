package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow

class ObtenerCategoriasUseCase(
    private val repository: CategoriaRepository
) {
    operator fun invoke(): Flow<List<Categoria>> {
        return repository.obtenerCategorias()
    }
}
