package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.model.TipoCategoria
import com.example.gastosapp.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow

class ObtenerCategoriasPorTipoUseCase(
    private val repository: CategoriaRepository
) {
    operator fun invoke(tipo: TipoCategoria): Flow<List<Categoria>> {
        return repository.obtenerCategoriasPorTipo(tipo)
    }
}
