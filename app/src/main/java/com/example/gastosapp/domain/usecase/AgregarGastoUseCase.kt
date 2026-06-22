package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.domain.repository.GastoRepository
import java.util.Calendar
import java.util.Date

class AgregarGastoUseCase(
    private val repository: GastoRepository
) {
    @Throws(Exception::class)
    suspend operator fun invoke(gasto: Gasto) {
        if (gasto.detalle.isBlank()) {
            throw Exception("El detalle no puede estar vacío.")
        }
        if (gasto.monto <= 0) {
            throw Exception("El monto debe ser mayor a cero.")
        }
        if (gasto.nombreDeUsuario.isBlank()) {
            throw Exception("El nombre de usuario no puede estar vacío.")
        }
        if (gasto.nombreCategoria.isBlank()) {
            throw Exception("La categoría no puede estar vacía.")
        }

        val calendar = Calendar.getInstance()
        
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, -15)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val limitPast = calendar.time
        
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, 15)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val limitFuture = calendar.time
        
        if (gasto.fecha.before(limitPast) || gasto.fecha.after(limitFuture)) {
            throw Exception("La fecha del gasto debe estar dentro del rango de 15 días en el pasado o futuro.")
        }

        repository.insertarGasto(gasto)
    }
}
