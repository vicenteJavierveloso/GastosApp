package com.example.gastosapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gastosapp.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias WHERE esDeMeta = 0 ORDER BY nombre ASC")
    fun obtenerCategorias(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE tipo = :tipo AND esDeMeta = 0 ORDER BY nombre ASC")
    fun obtenerCategoriasPorTipo(tipo: String): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerCategoriaPorNombre(nombre: String): Categoria?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarCategoria(categoria: Categoria)

    @Delete
    suspend fun eliminarCategoria(categoria: Categoria)
}
