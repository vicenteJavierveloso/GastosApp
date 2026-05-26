package com.example.gastosapp.domain.repository

import com.example.gastosapp.domain.model.Gasto
import kotlinx.coroutines.flow.Flow

interface GastoRepository {
    fun obtenerGastos(): Flow<List<Gasto>>
    suspend fun insertarGasto(gasto: Gasto)
    suspend fun eliminarGasto(gasto: Gasto)
}
