package com.example.gastosapp.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gastosapp.domain.model.Meta
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetasScreen(
    viewModel: MetasViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    var nombreCategoria by remember { mutableStateOf("") }
    var montoGoal by remember { mutableStateOf("") }
    var fechaLimite by remember { mutableStateOf(Date()) }

    var showAportarDialogFor by remember { mutableStateOf<Meta?>(null) }
    var showRetirarDialogFor by remember { mutableStateOf<Meta?>(null) }
    var dialogAmount by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metas de Ahorro") },
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
                value = nombreCategoria,
                onValueChange = { nombreCategoria = it },
                label = { Text("Nombre de la meta / Categoría") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = montoGoal,
                onValueChange = { montoGoal = it },
                label = { Text("Monto Objetivo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Limit Date Picker
            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = dateFormatter.format(fechaLimite),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha Límite") },
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
                            calendar.time = fechaLimite
                            val datePickerDialog = android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selectedCal = Calendar.getInstance()
                                    selectedCal.set(year, month, dayOfMonth)
                                    fechaLimite = selectedCal.time
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            // La fecha límite de la meta de ahorro debe ser a partir de hoy
                            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
                            datePickerDialog.show()
                        }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val targetMonto = montoGoal.toIntOrNull() ?: 0
                    viewModel.onEvent(
                        MetasEvent.AgregarMeta(
                            monto = targetMonto,
                            nombreCategoria = nombreCategoria,
                            fechaLimite = fechaLimite
                        )
                    )
                    if (state.error == null) {
                        nombreCategoria = ""
                        montoGoal = ""
                        fechaLimite = Date()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Meta de Ahorro")
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.metasProgress) { progress ->
                    MetaItem(
                        progress = progress,
                        onAportar = {
                            showAportarDialogFor = progress.meta
                            dialogAmount = ""
                        },
                        onRetirar = {
                            showRetirarDialogFor = progress.meta
                            dialogAmount = ""
                        },
                        onDelete = {
                            viewModel.onEvent(MetasEvent.EliminarMeta(progress.meta))
                        }
                    )
                }
            }
        }
    }

    // Dialogs for Aportar/Retirar
    if (showAportarDialogFor != null) {
        AlertDialog(
            onDismissRequest = { showAportarDialogFor = null },
            title = { Text("Aportar a: ${showAportarDialogFor!!.nombreCategoria}") },
            text = {
                Column {
                    Text("Ingrese el monto a aportar:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = dialogAmount,
                        onValueChange = { dialogAmount = it },
                        label = { Text("Monto") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = dialogAmount.toIntOrNull() ?: 0
                        viewModel.onEvent(MetasEvent.AportarFondos(showAportarDialogFor!!, amount))
                        showAportarDialogFor = null
                    }
                ) {
                    Text("Aportar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAportarDialogFor = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showRetirarDialogFor != null) {
        val progress = state.metasProgress.find { it.meta.codigoMeta == showRetirarDialogFor!!.codigoMeta }
        val montoActual = progress?.montoActual ?: 0
        AlertDialog(
            onDismissRequest = { showRetirarDialogFor = null },
            title = { Text("Retiro de Ahorros") },
            text = {
                Text("¿Está seguro de que desea retirar la totalidad del ahorro acumulado ($${montoActual}) para la meta '${showRetirarDialogFor!!.nombreCategoria}'?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onEvent(MetasEvent.RetirarFondos(showRetirarDialogFor!!))
                        showRetirarDialogFor = null
                    }
                ) {
                    Text("Retirar Todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRetirarDialogFor = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun MetaItem(
    progress: MetaProgress,
    onAportar: () -> Unit,
    onRetirar: () -> Unit,
    onDelete: () -> Unit
) {
    val meta = progress.meta
    val montoActual = progress.montoActual
    val reached = montoActual >= meta.monto
    val limitPassed = Date().after(meta.fechaLimite)
    val showWarning = !reached && limitPassed
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (reached) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            } else if (showWarning) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = meta.nombreCategoria,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Objetivo: $${meta.monto} | Ahorro: $${montoActual}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Límite: ${dateFormatter.format(meta.fechaLimite)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete) {
                    Text("X")
                }
            }

            if (showWarning) {
                Text(
                    text = "⚠️ ¡Tiempo límite excedido y meta no completada!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            } else if (reached) {
                Text(
                    text = "🎉 ¡Meta alcanzada con éxito!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAportar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Aportar")
                }
                Button(
                    onClick = onRetirar,
                    enabled = reached, // Solamente habilitado cuando la meta ha sido alcanzada
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Retirar")
                }
            }
        }
    }
}
