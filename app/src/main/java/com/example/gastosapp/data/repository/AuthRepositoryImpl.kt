package com.example.gastosapp.data.repository

import com.example.gastosapp.data.local.dao.UsuarioDao
import com.example.gastosapp.data.remote.auth.AuthRemoteDataSource
import com.example.gastosapp.domain.model.Usuario
import com.example.gastosapp.domain.repository.AuthRepository
import java.util.Date
import com.example.gastosapp.data.local.entity.Usuario as UsuarioEntity

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val usuarioDao: UsuarioDao
) : AuthRepository {
    override suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {
        val firebaseUser = authRemoteDataSource.iniciarSesion(correo, contrasena)
        val usuarioLocal = usuarioDao.obtenerUsuarioPorCorreo(firebaseUser.correo)
        val fechaInicioSesion = Date()

        val usuarioActualizado = if (usuarioLocal != null) {
            usuarioLocal.copy(
                nombre = firebaseUser.nombre ?: usuarioLocal.nombre,
                correo = firebaseUser.correo,
                ultimoInicioDeSesion = fechaInicioSesion
            )
        } else {
            UsuarioEntity(
                nombreUsuario = generarNombreUsuario(firebaseUser.correo),
                nombre = firebaseUser.nombre ?: generarNombreDesdeCorreo(firebaseUser.correo),
                correo = firebaseUser.correo,
                ultimoInicioDeSesion = fechaInicioSesion,
                contrasena = ""
            )
        }

        usuarioDao.insertarUsuario(usuarioActualizado)
        return usuarioActualizado.toDomain()
    }

    override suspend fun obtenerUsuarioActual(): Usuario? {
        val firebaseUser = authRemoteDataSource.obtenerUsuarioActual() ?: return null
        val usuarioLocal = usuarioDao.obtenerUsuarioPorCorreo(firebaseUser.correo)
        val fechaInicioSesion = Date()

        val usuarioActualizado = if (usuarioLocal != null) {
            usuarioLocal.copy(
                nombre = firebaseUser.nombre ?: usuarioLocal.nombre,
                correo = firebaseUser.correo,
                ultimoInicioDeSesion = fechaInicioSesion
            )
        } else {
            UsuarioEntity(
                nombreUsuario = generarNombreUsuario(firebaseUser.correo),
                nombre = firebaseUser.nombre ?: generarNombreDesdeCorreo(firebaseUser.correo),
                correo = firebaseUser.correo,
                ultimoInicioDeSesion = fechaInicioSesion,
                contrasena = ""
            )
        }

        usuarioDao.insertarUsuario(usuarioActualizado)
        return usuarioActualizado.toDomain()
    }

    private suspend fun generarNombreUsuario(correo: String): String {
        val base = normalizarNombreUsuario(correo.substringBefore("@").ifBlank { "usuario" })
        var candidato = base
        var contador = 1

        while (usuarioDao.obtenerUsuarioPorNombreUsuario(candidato) != null) {
            val sufijo = contador.toString()
            val maxBaseLength = MAX_NOMBRE_USUARIO_LENGTH - sufijo.length
            candidato = base.take(maxBaseLength.coerceAtLeast(1)) + sufijo
            contador++
        }

        return candidato
    }

    private fun normalizarNombreUsuario(valor: String): String {
        val normalizado = valor
            .lowercase()
            .filter { it.isLetterOrDigit() || it == '_' || it == '.' || it == '-' }
            .take(MAX_NOMBRE_USUARIO_LENGTH)

        return normalizado.ifBlank { "usuario" }
    }

    private fun generarNombreDesdeCorreo(correo: String): String {
        return correo.substringBefore("@").ifBlank { correo }.take(MAX_NOMBRE_LENGTH)
    }

    private fun UsuarioEntity.toDomain(): Usuario {
        return Usuario(
            nombreUsuario = nombreUsuario,
            nombre = nombre,
            correo = correo,
            ultimoInicioDeSesion = ultimoInicioDeSesion,
            contrasena = contrasena
        )
    }

    private companion object {
        const val MAX_NOMBRE_USUARIO_LENGTH = 30
        const val MAX_NOMBRE_LENGTH = 255
    }
}
