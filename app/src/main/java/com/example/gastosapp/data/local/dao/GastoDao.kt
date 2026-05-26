package com.example.gastosapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastosapp.data.local.entity.Gasto
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos ORDER BY fecha DESC, codigogasto DESC")
    fun obtenerGastos(): Flow<List<Gasto>>

    @Query("SELECT * FROM gastos WHERE codigogasto = :codigoGasto LIMIT 1")
    suspend fun obtenerGastoPorCodigo(codigoGasto: Int): Gasto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarGasto(gasto: Gasto)

    @Update
    suspend fun actualizarGasto(gasto: Gasto)

    @Delete
    suspend fun eliminarGasto(gasto: Gasto)

    @Query("DELETE FROM gastos WHERE codigogasto = :codigoGasto")
    suspend fun eliminarGastoPorCodigo(codigoGasto: Int)
}
