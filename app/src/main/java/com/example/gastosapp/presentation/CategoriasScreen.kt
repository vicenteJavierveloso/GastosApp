package com.example.gastosapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gastosapp.domain.model.Categoria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    viewModel: CategoriasViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var nombre by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Categorías") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<") // Icono de volver
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la categoría") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.onEvent(CategoriasEvent.AgregarCategoria(nombre))
                    if (state.error == null) {
                        nombre = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar Categoría")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(state.categorias) { categoria ->
                    CategoriaItem(
                        categoria = categoria,
                        onDelete = { viewModel.onEvent(CategoriasEvent.EliminarCategoria(categoria)) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriaItem(
    categoria: Categoria,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = categoria.nombre, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDelete) {
                Text("X")
            }
        }
    }
}
