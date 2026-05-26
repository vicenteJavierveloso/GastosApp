package com.example.gastosapp.domain.repository

import com.example.gastosapp.domain.model.Ingreso
import kotlinx.coroutines.flow.Flow

interface IngresoRepository {
    fun obtenerIngresos(): Flow<List<Ingreso>>
    suspend fun insertarIngreso(ingreso: Ingreso)
    suspend fun eliminarIngreso(ingreso: Ingreso)
}
