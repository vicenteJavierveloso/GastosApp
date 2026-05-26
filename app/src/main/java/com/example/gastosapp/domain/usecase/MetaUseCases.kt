package com.example.gastosapp.domain.usecase

data class MetaUseCases(
    val obtenerMetas: ObtenerMetasUseCase,
    val agregarMeta: AgregarMetaUseCase,
    val eliminarMeta: EliminarMetaUseCase
)
