package com.example.gastosapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerIngresosScreen(
    viewModel: IngresosViewModel,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val filteredIngresos = remember(state.ingresos, searchQuery, selectedCategory) {
        state.ingresos.filter { ingreso ->
            val matchesSearch = ingreso.detalle.contains(searchQuery, ignoreCase = true) ||
                    ingreso.nombreCategoria.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || ingreso.nombreCategoria == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registros de Ingresos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Text("+", style = MaterialTheme.typography.titleMedium)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search field
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por detalle o categoría...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category Filter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtrar por categoría:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Box {
                    Button(onClick = { categoryDropdownExpanded = true }) {
                        Text(selectedCategory ?: "Todas")
                    }
                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas") },
                            onClick = {
                                selectedCategory = null
                                categoryDropdownExpanded = false
                            }
                        )
                        state.categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                onClick = {
                                    selectedCategory = categoria.nombre
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (filteredIngresos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron registros de ingresos.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredIngresos) { ingreso ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = ingreso.detalle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Categoría: ${ingreso.nombreCategoria} | Usuario: ${ingreso.nombreDeUsuario}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = dateFormatter.format(ingreso.fecha),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Text(
                                    text = "+$${ingreso.monto}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerGastosScreen(
    viewModel: GastosViewModel,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val filteredGastos = remember(state.gastos, searchQuery, selectedCategory) {
        state.gastos.filter { gasto ->
            val matchesSearch = gasto.detalle.contains(searchQuery, ignoreCase = true) ||
                    gasto.nombreCategoria.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || gasto.nombreCategoria == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registros de Gastos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Text("+", style = MaterialTheme.typography.titleMedium)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search field
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por detalle o categoría...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category Filter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtrar por categoría:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Box {
                    Button(onClick = { categoryDropdownExpanded = true }) {
                        Text(selectedCategory ?: "Todas")
                    }
                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas") },
                            onClick = {
                                selectedCategory = null
                                categoryDropdownExpanded = false
                            }
                        )
                        state.categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                onClick = {
                                    selectedCategory = categoria.nombre
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (filteredGastos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron registros de gastos.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredGastos) { gasto ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = gasto.detalle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Categoría: ${gasto.nombreCategoria} | Usuario: ${gasto.nombreDeUsuario}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = dateFormatter.format(gasto.fecha),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Text(
                                    text = "-$${gasto.monto}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
