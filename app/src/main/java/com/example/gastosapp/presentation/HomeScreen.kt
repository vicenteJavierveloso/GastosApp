package com.example.gastosapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToExpenses: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gastos App - Menú Principal") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNavigateToExpenses,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Gestionar Gastos (CRUD)")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Placeholder para futuras funcionalidades
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                enabled = false
            ) {
                Text("Estadísticas (Próximamente)")
            }
        }
    }
}
