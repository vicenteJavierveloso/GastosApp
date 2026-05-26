package com.example.gastosapp.domain.repository

import com.example.gastosapp.domain.model.Meta
import kotlinx.coroutines.flow.Flow

interface MetaRepository {
    fun obtenerMetas(): Flow<List<Meta>>
    suspend fun insertarMeta(meta: Meta)
    suspend fun eliminarMeta(meta: Meta)
}
