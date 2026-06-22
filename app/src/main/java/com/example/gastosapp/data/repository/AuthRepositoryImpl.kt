package com.example.gastosapp.data.repository

import com.example.gastosapp.data.local.database.GastosDatabase
import com.example.gastosapp.data.remote.BackendClient
import com.example.gastosapp.domain.model.Usuario
import com.example.gastosapp.domain.repository.AuthRepository
import java.util.Date
import com.example.gastosapp.data.local.entity.Usuario as UsuarioEntity

class AuthRepositoryImpl(
    private val database: GastosDatabase
) : AuthRepository {
    private val usuarioDao = database.usuarioDao()
    private val categoriaDao = database.categoriaDao()
    private val gastoDao = database.gastoDao()
    private val ingresoDao = database.ingresoDao()
    private val metaDao = database.metaDao()

    override suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {
        // Sync with Ktor Backend
        var syncSuccess = BackendClient.login(correo, contrasena)
        val username = BackendClient.getNombreUsuario() ?: generarNombreUsuario(correo)

        if (!syncSuccess) {
            val name = generarNombreDesdeCorreo(correo)
            syncSuccess = BackendClient.register(username, name, correo, contrasena)
        }

        if (!syncSuccess) {
            throw Exception("Credenciales incorrectas o error en el servidor.")
        }

        try {
            database.clearAllTables()
            syncDataFromBackend(username)
        } catch (e: Exception) {
            // Ignore or log
        }

        val fechaInicioSesion = Date()
        val usuarioActualizado = UsuarioEntity(
            nombreUsuario = username,
            nombre = BackendClient.getNombreRealUsuario() ?: generarNombreDesdeCorreo(correo),
            correo = BackendClient.getCorreoUsuario() ?: correo,
            ultimoInicioDeSesion = fechaInicioSesion,
            contrasena = ""
        )

        usuarioDao.insertarUsuario(usuarioActualizado)
        return usuarioActualizado.toDomain()
    }

    override suspend fun obtenerUsuarioActual(): Usuario? {
        if (!BackendClient.hasToken()) {
            return null
        }

        val username = BackendClient.getNombreUsuario() ?: return null
        val usuarioLocal = usuarioDao.obtenerUsuarioPorNombreUsuario(username)
        val email = usuarioLocal?.correo ?: (BackendClient.getCorreoUsuario() ?: "")
        val name = usuarioLocal?.nombre ?: (BackendClient.getNombreRealUsuario() ?: generarNombreDesdeCorreo(email))

        try {
            database.clearAllTables()
            syncDataFromBackend(username)
        } catch (e: Exception) {
            // Ignore or log
        }

        val fechaInicioSesion = Date()
        val usuarioActualizado = UsuarioEntity(
            nombreUsuario = username,
            nombre = name,
            correo = email,
            ultimoInicioDeSesion = fechaInicioSesion,
            contrasena = ""
        )

        usuarioDao.insertarUsuario(usuarioActualizado)
        return usuarioActualizado.toDomain()
    }

    private suspend fun syncDataFromBackend(username: String) {
        // Fetch all categories
        val categoriasRemote = BackendClient.getCategorias()
        categoriasRemote.forEach { cat ->
            categoriaDao.insertarCategoria(
                com.example.gastosapp.data.local.entity.Categoria(
                    nombre = cat.nombre,
                    tipo = cat.tipo.name,
                    esDeMeta = cat.esDeMeta
                )
            )
        }

        // Fetch all incomes
        val ingresosRemote = BackendClient.getIngresos()
        ingresosRemote.forEach { ing ->
            if (usuarioDao.obtenerUsuarioPorNombreUsuario(ing.nombreDeUsuario) == null) {
                usuarioDao.insertarUsuario(
                    UsuarioEntity(
                        nombreUsuario = ing.nombreDeUsuario,
                        nombre = ing.nombreDeUsuario,
                        correo = "",
                        ultimoInicioDeSesion = Date(),
                        contrasena = ""
                    )
                )
            }
            ingresoDao.insertarIngreso(
                com.example.gastosapp.data.local.entity.Ingreso(
                    codigoIngreso = ing.codigoIngreso,
                    monto = ing.monto,
                    detalle = ing.detalle,
                    nombreDeUsuario = ing.nombreDeUsuario,
                    nombreCategoria = ing.nombreCategoria,
                    fecha = ing.fecha
                )
            )
        }

        // Fetch all expenses
        val gastosRemote = BackendClient.getGastos()
        gastosRemote.forEach { gasto ->
            if (usuarioDao.obtenerUsuarioPorNombreUsuario(gasto.nombreDeUsuario) == null) {
                usuarioDao.insertarUsuario(
                    UsuarioEntity(
                        nombreUsuario = gasto.nombreDeUsuario,
                        nombre = gasto.nombreDeUsuario,
                        correo = "",
                        ultimoInicioDeSesion = Date(),
                        contrasena = ""
                    )
                )
            }
            gastoDao.insertarGasto(
                com.example.gastosapp.data.local.entity.Gasto(
                    codigoGasto = gasto.codigoGasto,
                    monto = gasto.monto,
                    detalle = gasto.detalle,
                    nombreDeUsuario = gasto.nombreDeUsuario,
                    nombreCategoria = gasto.nombreCategoria,
                    fecha = gasto.fecha
                )
            )
        }

        // Fetch all metas
        val metasRemote = BackendClient.getMetas()
        metasRemote.forEach { meta ->
            if (usuarioDao.obtenerUsuarioPorNombreUsuario(meta.nombreDeUsuario) == null) {
                usuarioDao.insertarUsuario(
                    UsuarioEntity(
                        nombreUsuario = meta.nombreDeUsuario,
                        nombre = meta.nombreDeUsuario,
                        correo = "",
                        ultimoInicioDeSesion = Date(),
                        contrasena = ""
                    )
                )
            }
            metaDao.insertarMeta(
                com.example.gastosapp.data.local.entity.Meta(
                    codigoMeta = meta.codigoMeta,
                    monto = meta.monto,
                    nombreDeUsuario = meta.nombreDeUsuario,
                    nombreCategoria = meta.nombreCategoria,
                    fechaLimite = meta.fechaLimite,
                    activa = meta.activa
                )
            )
        }
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
