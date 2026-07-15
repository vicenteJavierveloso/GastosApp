package com.example.gastosapp.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.model.TipoCategoria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    viewModel: CategoriasViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(TipoCategoria.GASTO) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Categorías") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("Tipo de categoría:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = tipo == TipoCategoria.GASTO,
                        onClick = { tipo = TipoCategoria.GASTO }
                    )
                    Text("Gasto", modifier = Modifier.clickable { tipo = TipoCategoria.GASTO })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = tipo == TipoCategoria.INGRESO,
                        onClick = { tipo = TipoCategoria.INGRESO }
                    )
                    Text("Ingreso", modifier = Modifier.clickable { tipo = TipoCategoria.INGRESO })
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.onEvent(CategoriasEvent.AgregarCategoria(nombre, tipo))
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
            Column {
                Text(text = categoria.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (categoria.tipo == TipoCategoria.GASTO) "Gasto" else "Ingreso",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (categoria.tipo == TipoCategoria.GASTO) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Text("X")
            }
        }
    }
}
