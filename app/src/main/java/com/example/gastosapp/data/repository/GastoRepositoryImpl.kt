package com.example.gastosapp.data.repository

import com.example.gastosapp.data.local.dao.CategoriaDao
import com.example.gastosapp.data.local.dao.GastoDao
import com.example.gastosapp.data.local.dao.UsuarioDao
import com.example.gastosapp.data.local.entity.Categoria
import com.example.gastosapp.data.local.entity.Usuario
import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.domain.repository.GastoRepository
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.gastosapp.data.local.entity.Gasto as GastoEntity

class GastoRepositoryImpl(
    private val gastoDao: GastoDao,
    private val categoriaDao: CategoriaDao,
    private val usuarioDao: UsuarioDao
) : GastoRepository {
    override fun obtenerGastos(): Flow<List<Gasto>> {
        return gastoDao.obtenerGastos().map { gastos ->
            gastos.map { it.toDomain() }
        }
    }

    override suspend fun insertarGasto(gasto: Gasto) {
        categoriaDao.insertarCategoria(Categoria(nombre = gasto.nombreCategoria, tipo = "GASTO"))
        if (usuarioDao.obtenerUsuarioPorNombreUsuario(gasto.nombreDeUsuario) == null) {
            usuarioDao.insertarUsuario(
                Usuario(
                    nombreUsuario = gasto.nombreDeUsuario,
                    nombre = gasto.nombreDeUsuario,
                    correo = "",
                    ultimoInicioDeSesion = Date(),
                    contrasena = ""
                )
            )
        }
        gastoDao.insertarGasto(gasto.toEntity())
    }

    override suspend fun eliminarGasto(gasto: Gasto) {
        gastoDao.eliminarGasto(gasto.toEntity())
    }

    private fun GastoEntity.toDomain(): Gasto {
        return Gasto(
            codigoGasto = codigoGasto,
            monto = monto,
            detalle = detalle,
            nombreDeUsuario = nombreDeUsuario,
            nombreCategoria = nombreCategoria,
            fecha = fecha
        )
    }

    private fun Gasto.toEntity(): GastoEntity {
        return GastoEntity(
            codigoGasto = codigoGasto,
            monto = monto,
            detalle = detalle,
            nombreDeUsuario = nombreDeUsuario,
            nombreCategoria = nombreCategoria,
            fecha = fecha
        )
    }
}
