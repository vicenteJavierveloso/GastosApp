package com.example.gastosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.usecase.CategoriaUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CategoriasViewModel(
    private val categoriaUseCases: CategoriaUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriasState())
    val state: StateFlow<CategoriasState> = _state.asStateFlow()

    init {
        obtenerCategorias()
    }

    fun onEvent(event: CategoriasEvent) {
        when (event) {
            is CategoriasEvent.AgregarCategoria -> {
                agregarCategoria(event.nombre)
            }
            is CategoriasEvent.EliminarCategoria -> {
                eliminarCategoria(event.categoria)
            }
        }
    }

    private fun obtenerCategorias() {
        categoriaUseCases.obtenerCategorias().onEach { categorias ->
            _state.value = state.value.copy(
                categorias = categorias
            )
        }.launchIn(viewModelScope)
    }

    private fun agregarCategoria(nombre: String) {
        viewModelScope.launch {
            try {
                val categoria = Categoria(nombre = nombre)
                categoriaUseCases.agregarCategoria(categoria)
                _state.value = _state.value.copy(error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error desconocido")
            }
        }
    }

    private fun eliminarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            try {
                categoriaUseCases.eliminarCategoria(categoria)
                _state.value = _state.value.copy(error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "No se pudo eliminar la categoría. Asegúrese de que no esté en uso.")
            }
        }
    }
}
