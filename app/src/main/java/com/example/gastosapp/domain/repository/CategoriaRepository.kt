package com.example.gastosapp.domain.repository

import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.model.TipoCategoria
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    fun obtenerCategorias(): Flow<List<Categoria>>
    fun obtenerCategoriasPorTipo(tipo: TipoCategoria): Flow<List<Categoria>>
    suspend fun insertarCategoria(categoria: Categoria)
    suspend fun eliminarCategoria(categoria: Categoria)
}
