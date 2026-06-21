package com.example.gastosapp.domain.usecase

data class CategoriaUseCases(
    val obtenerCategorias: ObtenerCategoriasUseCase,
    val agregarCategoria: AgregarCategoriaUseCase,
    val eliminarCategoria: EliminarCategoriaUseCase
)
