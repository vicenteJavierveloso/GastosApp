package com.example.gastosapp.data.repository

import com.example.gastosapp.data.local.dao.CategoriaDao
import com.example.gastosapp.data.local.dao.MetaDao
import com.example.gastosapp.data.local.dao.UsuarioDao
import com.example.gastosapp.data.local.entity.Categoria
import com.example.gastosapp.data.local.entity.Usuario
import com.example.gastosapp.domain.model.Meta
import com.example.gastosapp.domain.repository.MetaRepository
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.gastosapp.data.local.entity.Meta as MetaEntity

class MetaRepositoryImpl(
    private val metaDao: MetaDao,
    private val usuarioDao: UsuarioDao,
    private val categoriaDao: CategoriaDao
) : MetaRepository {
    override fun obtenerMetas(): Flow<List<Meta>> {
        return metaDao.obtenerMetas().map { metas ->
            metas.map { it.toDomain() }
        }
    }

    override suspend fun insertarMeta(meta: Meta) {
        // Sync categories and goal to backend first
        com.example.gastosapp.data.remote.BackendClient.insertCategoria(
            com.example.gastosapp.domain.model.Categoria(nombre = meta.nombreCategoria, tipo = com.example.gastosapp.domain.model.TipoCategoria.GASTO, esDeMeta = true)
        )
        com.example.gastosapp.data.remote.BackendClient.insertCategoria(
            com.example.gastosapp.domain.model.Categoria(nombre = meta.nombreCategoria, tipo = com.example.gastosapp.domain.model.TipoCategoria.INGRESO, esDeMeta = true)
        )
        val serverId = com.example.gastosapp.data.remote.BackendClient.insertMeta(meta)
        val finalMeta = if (serverId != null) {
            meta.copy(codigoMeta = serverId)
        } else {
            meta
        }

        if (usuarioDao.obtenerUsuarioPorNombreUsuario(meta.nombreDeUsuario) == null) {
            usuarioDao.insertarUsuario(
                Usuario(
                    nombreUsuario = meta.nombreDeUsuario,
                    nombre = meta.nombreDeUsuario,
                    correo = "",
                    ultimoInicioDeSesion = Date(),
                    contrasena = ""
                )
            )
        }
        
        // Crear las dos categorías para la meta
        categoriaDao.insertarCategoria(Categoria(nombre = meta.nombreCategoria, tipo = "GASTO", esDeMeta = true))
        categoriaDao.insertarCategoria(Categoria(nombre = meta.nombreCategoria, tipo = "INGRESO", esDeMeta = true))
        
        metaDao.insertarMeta(finalMeta.toEntity())
    }

    override suspend fun eliminarMeta(meta: Meta) {
        com.example.gastosapp.data.remote.BackendClient.deleteMeta(meta.codigoMeta)
        com.example.gastosapp.data.remote.BackendClient.deleteCategoria(meta.nombreCategoria, "GASTO")
        com.example.gastosapp.data.remote.BackendClient.deleteCategoria(meta.nombreCategoria, "INGRESO")

        metaDao.eliminarMeta(meta.toEntity())
        
        // Eliminar las categorías de la meta
        categoriaDao.eliminarCategoria(Categoria(nombre = meta.nombreCategoria, tipo = "GASTO", esDeMeta = true))
        categoriaDao.eliminarCategoria(Categoria(nombre = meta.nombreCategoria, tipo = "INGRESO", esDeMeta = true))
    }

    override suspend fun actualizarMeta(meta: Meta) {
        com.example.gastosapp.data.remote.BackendClient.updateMeta(meta)
        metaDao.actualizarMeta(meta.toEntity())
    }

    private fun MetaEntity.toDomain(): Meta {
        return Meta(
            codigoMeta = codigoMeta,
            monto = monto,
            nombreDeUsuario = nombreDeUsuario,
            nombreCategoria = nombreCategoria,
            fechaLimite = fechaLimite,
            activa = activa
        )
    }

    private fun Meta.toEntity(): MetaEntity {
        return MetaEntity(
            codigoMeta = codigoMeta,
            monto = monto,
            nombreDeUsuario = nombreDeUsuario,
            nombreCategoria = nombreCategoria,
            fechaLimite = fechaLimite,
            activa = activa
        )
    }
}
