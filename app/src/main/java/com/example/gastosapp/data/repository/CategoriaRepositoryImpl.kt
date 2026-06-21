package com.example.gastosapp.data.repository

import com.example.gastosapp.data.local.dao.CategoriaDao
import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.gastosapp.data.local.entity.Categoria as CategoriaEntity

class CategoriaRepositoryImpl(
    private val categoriaDao: CategoriaDao
) : CategoriaRepository {
    override fun obtenerCategorias(): Flow<List<Categoria>> {
        return categoriaDao.obtenerCategorias().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertarCategoria(categoria: Categoria) {
        categoriaDao.insertarCategoria(categoria.toEntity())
    }

    override suspend fun eliminarCategoria(categoria: Categoria) {
        categoriaDao.eliminarCategoria(categoria.toEntity())
    }

    private fun CategoriaEntity.toDomain(): Categoria {
        return Categoria(nombre = nombre)
    }

    private fun Categoria.toEntity(): CategoriaEntity {
        return CategoriaEntity(nombre = nombre)
    }
}
