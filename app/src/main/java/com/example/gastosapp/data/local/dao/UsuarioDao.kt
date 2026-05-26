package com.example.gastosapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastosapp.data.local.entity.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios ORDER BY nombreusuario ASC")
    fun obtenerUsuarios(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE nombreusuario = :nombreUsuario LIMIT 1")
    suspend fun obtenerUsuarioPorNombreUsuario(nombreUsuario: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario)

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    @Delete
    suspend fun eliminarUsuario(usuario: Usuario)

    @Query("DELETE FROM usuarios WHERE nombreusuario = :nombreUsuario")
    suspend fun eliminarUsuarioPorNombreUsuario(nombreUsuario: String)
}
