package com.example.gastosapp.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToGastos: () -> Unit,
    onNavigateToIngresos: () -> Unit,
    onNavigateToCategorias: () -> Unit,
    onNavigateToMetas: () -> Unit,
    onNavigateToVerIngresos: () -> Unit,
    onNavigateToVerGastos: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            BalanceCard(
                balance = state.balance,
                formatter = formatter
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Ingresos",
                    value = formatter.format(state.totalIngresos),
                    detail = "${state.cantidadIngresos} registros",
                    accentColor = Color(0xFF1B7F5C),
                    onClick = onNavigateToVerIngresos,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Gastos",
                    value = formatter.format(state.totalGastos),
                    detail = "${state.cantidadGastos} registros",
                    accentColor = Color(0xFFC2410C),
                    onClick = onNavigateToVerGastos,
                    modifier = Modifier.weight(1f)
                )
            }

            StatCard(
                title = "Categoria destacada",
                value = state.principalCategoriaGasto ?: "Sin gastos",
                detail = "Mayor monto acumulado",
                accentColor = Color(0xFF3B6EA8),
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Gestionar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            ManagementOption(
                title = "Gastos",
                subtitle = "Registrar y revisar egresos",
                icon = Icons.Filled.ShoppingCart,
                accentColor = Color(0xFFC2410C),
                onClick = onNavigateToGastos
            )
            ManagementOption(
                title = "Ingresos",
                subtitle = "Registrar y revisar entradas",
                icon = Icons.Filled.Add,
                accentColor = Color(0xFF1B7F5C),
                onClick = onNavigateToIngresos
            )
            ManagementOption(
                title = "Categorias",
                subtitle = "Organizar movimientos",
                icon = Icons.Filled.List,
                accentColor = Color(0xFF3B6EA8),
                onClick = onNavigateToCategorias
            )
            ManagementOption(
                title = "Metas de Ahorro",
                subtitle = "Establecer y gestionar objetivos",
                icon = Icons.Filled.Star,
                accentColor = Color(0xFFEAB308),
                onClick = onNavigateToMetas
            )
        }
    }
}

@Composable
private fun BalanceCard(
    balance: Int,
    formatter: NumberFormat
) {
    val positive = balance >= 0
    val accent = if (positive) Color(0xFF1B7F5C) else Color(0xFFC2410C)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Balance actual",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatter.format(balance),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = accent
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    detail: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ManagementOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.14f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
