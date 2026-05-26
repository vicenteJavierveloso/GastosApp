package com.example.gastosapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastosapp.data.local.entity.Meta
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaDao {
    @Query("SELECT * FROM metas ORDER BY codigometa DESC")
    fun obtenerMetas(): Flow<List<Meta>>

    @Query("SELECT * FROM metas WHERE codigometa = :codigoMeta LIMIT 1")
    suspend fun obtenerMetaPorCodigo(codigoMeta: Int): Meta?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMeta(meta: Meta)

    @Update
    suspend fun actualizarMeta(meta: Meta)

    @Delete
    suspend fun eliminarMeta(meta: Meta)

    @Query("DELETE FROM metas WHERE codigometa = :codigoMeta")
    suspend fun eliminarMetaPorCodigo(codigoMeta: Int)
}
