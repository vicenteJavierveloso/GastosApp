package com.example.gastosapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastosapp.data.local.entity.Ingreso
import kotlinx.coroutines.flow.Flow

@Dao
interface IngresoDao {
    @Query("SELECT * FROM ingresos ORDER BY fecha DESC, codigoingreso DESC")
    fun obtenerIngresos(): Flow<List<Ingreso>>

    @Query("SELECT * FROM ingresos WHERE codigoingreso = :codigoIngreso LIMIT 1")
    suspend fun obtenerIngresoPorCodigo(codigoIngreso: Int): Ingreso?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarIngreso(ingreso: Ingreso)

    @Update
    suspend fun actualizarIngreso(ingreso: Ingreso)

    @Delete
    suspend fun eliminarIngreso(ingreso: Ingreso)

    @Query("DELETE FROM ingresos WHERE codigoingreso = :codigoIngreso")
    suspend fun eliminarIngresoPorCodigo(codigoIngreso: Int)
}
