package com.example.gastosapp.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gastosapp.domain.model.Ingreso

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresosScreen(
    viewModel: IngresosViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var detalle by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var nombreCategoria by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Ingresos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<")
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

            Text(
                text = "Usuario actual: ${state.nombreDeUsuario.ifBlank { "Cargando..." }}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = detalle,
                onValueChange = { detalle = it },
                label = { Text("Detalle") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Categorías dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = nombreCategoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar Categoría") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { dropdownExpanded = true },
                    enabled = false, // Deshabilitar teclado pero permitir click
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                // Usar una capa transparente clickeable para abrir el menú
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { dropdownExpanded = true }
                )
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.categorias.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Sin categorías. Agréguelas primero.") },
                            onClick = { dropdownExpanded = false }
                        )
                    } else {
                        state.categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                onClick = {
                                    nombreCategoria = categoria.nombre
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val montoInt = monto.toIntOrNull() ?: 0
                    viewModel.onEvent(
                        IngresosEvent.AgregarIngreso(
                            detalle = detalle,
                            monto = montoInt,
                            nombreCategoria = nombreCategoria
                        )
                    )
                    if (state.error == null) {
                        detalle = ""
                        monto = ""
                        nombreCategoria = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar Ingreso")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(state.ingresos) { ingreso ->
                    IngresoItem(
                        ingreso = ingreso,
                        onDelete = { viewModel.onEvent(IngresosEvent.EliminarIngreso(ingreso)) }
                    )
                }
            }
        }
    }
}

@Composable
fun IngresoItem(
    ingreso: Ingreso,
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
                Text(text = ingreso.detalle, style = MaterialTheme.typography.titleMedium)
                Text(text = "$${ingreso.monto}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${ingreso.nombreCategoria} - ${ingreso.nombreDeUsuario}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Text("X")
            }
        }
    }
}
