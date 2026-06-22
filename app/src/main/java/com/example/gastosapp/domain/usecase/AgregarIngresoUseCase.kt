package com.example.gastosapp.domain.usecase

import com.example.gastosapp.domain.model.Ingreso
import com.example.gastosapp.domain.repository.IngresoRepository
import java.util.Calendar
import java.util.Date

class AgregarIngresoUseCase(
    private val repository: IngresoRepository
) {
    @Throws(Exception::class)
    suspend operator fun invoke(ingreso: Ingreso) {
        if (ingreso.detalle.isBlank()) {
            throw Exception("El detalle no puede estar vacío.")
        }
        if (ingreso.monto <= 0) {
            throw Exception("El monto debe ser mayor a cero.")
        }
        if (ingreso.nombreDeUsuario.isBlank()) {
            throw Exception("El nombre de usuario no puede estar vacío.")
        }
        if (ingreso.nombreCategoria.isBlank()) {
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
        
        if (ingreso.fecha.before(limitPast) || ingreso.fecha.after(limitFuture)) {
            throw Exception("La fecha del ingreso debe estar dentro del rango de 15 días en el pasado o futuro.")
        }

        repository.insertarIngreso(ingreso)
    }
}
