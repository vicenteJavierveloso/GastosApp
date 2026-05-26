package com.example.gastosapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gastosapp.domain.model.Gasto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(
    viewModel: GastosViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var detalle by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var nombreDeUsuario by remember { mutableStateOf("") }
    var nombreCategoria by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Gastos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<") // Placeholder para icono de volver
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
            TextField(
                value = nombreDeUsuario,
                onValueChange = { nombreDeUsuario = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = nombreCategoria,
                onValueChange = { nombreCategoria = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val montoInt = monto.toIntOrNull() ?: 0
                    viewModel.onEvent(
                        GastosEvent.AgregarGasto(
                            detalle = detalle,
                            monto = montoInt,
                            nombreDeUsuario = nombreDeUsuario,
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
                Text("Agregar Gasto")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(state.gastos) { gasto ->
                    GastoItem(
                        gasto = gasto,
                        onDelete = { viewModel.onEvent(GastosEvent.EliminarGasto(gasto)) }
                    )
                }
            }
        }
    }
}

@Composable
fun GastoItem(
    gasto: Gasto,
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
                Text(text = gasto.detalle, style = MaterialTheme.typography.titleMedium)
                Text(text = "$${gasto.monto}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${gasto.nombreCategoria} - ${gasto.nombreDeUsuario}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Text("X")
            }
        }
    }
}
