package com.example.gastosapp.data.repository

import com.example.gastosapp.data.local.dao.CategoriaDao
import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.model.TipoCategoria
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

    override fun obtenerCategoriasPorTipo(tipo: TipoCategoria): Flow<List<Categoria>> {
        return categoriaDao.obtenerCategoriasPorTipo(tipo.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertarCategoria(categoria: Categoria) {
        com.example.gastosapp.data.remote.BackendClient.insertCategoria(categoria)
        categoriaDao.insertarCategoria(categoria.toEntity())
    }

    override suspend fun eliminarCategoria(categoria: Categoria) {
        com.example.gastosapp.data.remote.BackendClient.deleteCategoria(categoria.nombre, categoria.tipo.name)
        categoriaDao.eliminarCategoria(categoria.toEntity())
    }

    private fun CategoriaEntity.toDomain(): Categoria {
        return Categoria(
            nombre = nombre,
            tipo = try { TipoCategoria.valueOf(tipo) } catch (e: Exception) { TipoCategoria.GASTO },
            esDeMeta = esDeMeta
        )
    }

    private fun Categoria.toEntity(): CategoriaEntity {
        return CategoriaEntity(
            nombre = nombre,
            tipo = tipo.name,
            esDeMeta = esDeMeta
        )
    }
}
