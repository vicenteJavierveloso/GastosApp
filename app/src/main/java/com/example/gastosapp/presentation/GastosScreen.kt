package com.example.gastosapp.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.presentation.components.LoadingModal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(
    viewModel: GastosViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var detalle by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var nombreCategoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(Date()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Gastos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LoadingModal(isLoading = state.isLoading)
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

            // Date picker field
            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = dateFormatter.format(fecha),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            calendar.time = fecha
                            val datePickerDialog = android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selectedCal = Calendar.getInstance()
                                    selectedCal.set(year, month, dayOfMonth)
                                    fecha = selectedCal.time
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            val minCal = Calendar.getInstance()
                            minCal.add(Calendar.DAY_OF_YEAR, -15)
                            datePickerDialog.datePicker.minDate = minCal.timeInMillis

                            val maxCal = Calendar.getInstance()
                            maxCal.add(Calendar.DAY_OF_YEAR, 15)
                            datePickerDialog.datePicker.maxDate = maxCal.timeInMillis

                            datePickerDialog.show()
                        }
                )
            }
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
                        GastosEvent.AgregarGasto(
                            detalle = detalle,
                            monto = montoInt,
                            nombreCategoria = nombreCategoria,
                            fecha = fecha
                        )
                    )
                    if (state.error == null) {
                        detalle = ""
                        monto = ""
                        nombreCategoria = ""
                        fecha = Date()
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
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
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
                Text(
                    text = dateFormatter.format(gasto.fecha),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onDelete) {
                Text("X")
            }
        }
    }
}
