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
        
        metaDao.insertarMeta(meta.toEntity())
    }

    override suspend fun eliminarMeta(meta: Meta) {
        metaDao.eliminarMeta(meta.toEntity())
        
        // Eliminar las categorías de la meta
        categoriaDao.eliminarCategoria(Categoria(nombre = meta.nombreCategoria, tipo = "GASTO", esDeMeta = true))
        categoriaDao.eliminarCategoria(Categoria(nombre = meta.nombreCategoria, tipo = "INGRESO", esDeMeta = true))
    }

    override suspend fun actualizarMeta(meta: Meta) {
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
