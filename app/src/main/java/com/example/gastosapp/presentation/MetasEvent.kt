package com.example.gastosapp.presentation

import com.example.gastosapp.domain.model.Meta
import java.util.Date

sealed class MetasEvent {
    data class AgregarMeta(
        val monto: Int,
        val nombreCategoria: String,
        val fechaLimite: Date
    ) : MetasEvent()

    data class EliminarMeta(val meta: Meta) : MetasEvent()

    data class AportarFondos(
        val meta: Meta,
        val monto: Int
    ) : MetasEvent()

    data class RetirarFondos(
        val meta: Meta
    ) : MetasEvent()

    data class DesactivarMeta(val meta: Meta) : MetasEvent()
    data class ActivarMeta(val meta: Meta) : MetasEvent()
}
