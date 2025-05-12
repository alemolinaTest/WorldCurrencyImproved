package com.amolina.worldcurrency.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amolina.worldcurrency.presentation.ui.component.CurrencySelector
import com.amolina.worldcurrency.presentation.ui.event.CurrencyUiEvent
import com.amolina.worldcurrency.presentation.ui.state.CurrencyUiState
import com.amolina.worldcurrency.presentation.viewmodel.CurrencyViewModel
import java.text.DecimalFormat

@Composable
fun CurrencyScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: CurrencyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentState = state

    // Mostrar Snackbar en caso de error
    if (currentState is CurrencyUiState.Error) {
        LaunchedEffect(currentState.message) {
            snackbarHostState.showSnackbar(
                message = currentState.message,
                withDismissAction = true
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding)) {
            if (currentState is CurrencyUiState.Success) {
                val data = currentState.data

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Currency Converter",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    if (data.isFromCache) {
                        item {
                            Text(
                                text = "Estás en modo sin conexión",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    item {
                        CurrencySelector(
                            label = "From",
                            currencies = data.availableCurrencies,
                            selected = data.selectedFrom,
                            exclude = data.selectedTo?.code,
                            onSelect = {
                                viewModel.onEvent(CurrencyUiEvent.OnFromCurrencySelected(it.code))
                            }
                        )
                    }

                    item {
                        CurrencySelector(
                            label = "To",
                            currencies = data.availableCurrencies,
                            selected = data.selectedTo,
                            exclude = data.selectedFrom?.code,
                            onSelect = {
                                viewModel.onEvent(CurrencyUiEvent.OnToCurrencySelected(it.code))
                            }
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = data.amount,
                            onValueChange = {
                                val filtered = it
                                    .filter { c -> c.isDigit() || c == '.' }
                                    .let { v ->
                                        val parts = v.split(".")
                                        when {
                                            parts.size > 2 -> parts[0]
                                            parts.size == 2 -> parts[0] + "." + parts[1].take(2)
                                            else -> v
                                        }
                                    }
                                viewModel.onEvent(CurrencyUiEvent.OnAmountChanged(filtered))
                            },
                            label = { Text("Amount") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Button(
                            onClick = { viewModel.onEvent(CurrencyUiEvent.OnConvert) },
                            enabled = data.selectedFrom != null && data.selectedTo != null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Convert")
                        }
                    }

                    item {
                        if (data.result.isNotBlank()) {
                            val number = data.result.toDoubleOrNull()
                            val formatted = number?.let {
                                val formatter = DecimalFormat("#,##0.##")
                                formatter.format(it)
                            } ?: "Invalid"

                            Text(
                                text = "${data.selectedTo?.code ?: ""}: $formatted",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = { navController.navigate("history") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View History")
                        }
                    }
                }
            }

            // Cargando como overlay
            if (currentState is CurrencyUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}