package com.example.gastosapp.data.repository

import com.example.gastosapp.data.local.dao.CategoriaDao
import com.example.gastosapp.data.local.dao.IngresoDao
import com.example.gastosapp.data.local.dao.UsuarioDao
import com.example.gastosapp.data.local.entity.Categoria
import com.example.gastosapp.data.local.entity.Usuario
import com.example.gastosapp.domain.model.Ingreso
import com.example.gastosapp.domain.repository.IngresoRepository
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.gastosapp.data.local.entity.Ingreso as IngresoEntity

class IngresoRepositoryImpl(
    private val ingresoDao: IngresoDao,
    private val categoriaDao: CategoriaDao,
    private val usuarioDao: UsuarioDao
) : IngresoRepository {
    override fun obtenerIngresos(): Flow<List<Ingreso>> {
        return ingresoDao.obtenerIngresos().map { ingresos ->
            ingresos.map { it.toDomain() }
        }
    }

    override suspend fun insertarIngreso(ingreso: Ingreso) {
        categoriaDao.insertarCategoria(Categoria(nombre = ingreso.nombreCategoria))
        if (usuarioDao.obtenerUsuarioPorNombreUsuario(ingreso.nombreDeUsuario) == null) {
            usuarioDao.insertarUsuario(
                Usuario(
                    nombreUsuario = ingreso.nombreDeUsuario,
                    nombre = ingreso.nombreDeUsuario,
                    correo = "",
                    ultimoInicioDeSesion = Date(),
                    contrasena = ""
                )
            )
        }
        ingresoDao.insertarIngreso(ingreso.toEntity())
    }

    override suspend fun eliminarIngreso(ingreso: Ingreso) {
        ingresoDao.eliminarIngreso(ingreso.toEntity())
    }

    private fun IngresoEntity.toDomain(): Ingreso {
        return Ingreso(
            codigoIngreso = codigoIngreso,
            monto = monto,
            detalle = detalle,
            nombreDeUsuario = nombreDeUsuario,
            nombreCategoria = nombreCategoria,
            fecha = fecha
        )
    }

    private fun Ingreso.toEntity(): IngresoEntity {
        return IngresoEntity(
            codigoIngreso = codigoIngreso,
            monto = monto,
            detalle = detalle,
            nombreDeUsuario = nombreDeUsuario,
            nombreCategoria = nombreCategoria,
            fecha = fecha
        )
    }
}
