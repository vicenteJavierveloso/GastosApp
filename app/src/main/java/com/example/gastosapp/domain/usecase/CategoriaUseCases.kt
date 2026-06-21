package com.example.gastosapp.domain.usecase

data class CategoriaUseCases(
    val obtenerCategorias: ObtenerCategoriasUseCase,
    val obtenerCategoriasPorTipo: ObtenerCategoriasPorTipoUseCase,
    val agregarCategoria: AgregarCategoriaUseCase,
    val eliminarCategoria: EliminarCategoriaUseCase
)
