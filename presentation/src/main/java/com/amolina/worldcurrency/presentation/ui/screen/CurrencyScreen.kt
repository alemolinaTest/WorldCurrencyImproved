package com.amolina.worldcurrency.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amolina.worldcurrency.domain.model.Currency
import com.amolina.worldcurrency.presentation.ui.component.CurrencySelector
import com.amolina.worldcurrency.presentation.ui.event.CurrencyUiEvent
import com.amolina.worldcurrency.presentation.viewmodel.CurrencyViewModel
import java.text.DecimalFormat

@Composable
fun CurrencyScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: CurrencyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Currency Converter",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            CurrencySelector(
                label = "From",
                currencies = state.availableCurrencies,
                selected = state.selectedFrom,
                exclude = state.selectedTo?.code,
                onSelect = { viewModel.onEvent(CurrencyUiEvent.OnFromCurrencySelected(it.code)) }
            )
        }

        item {
            CurrencySelector(
                label = "To",
                currencies = state.availableCurrencies,
                selected = state.selectedTo,
                exclude = state.selectedFrom?.code,
                onSelect = { viewModel.onEvent(CurrencyUiEvent.OnToCurrencySelected(it.code)) }
            )
        }

        item {
            OutlinedTextField(
                value = state.amount,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() || char == '.' }
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
                enabled = !state.isLoading && state.selectedFrom != null && state.selectedTo != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Convert")
            }
        }

        item {
            if (state.result.isNotBlank()) {
                val number: Double = state.result.toDouble() // si es String

                val formatted = DecimalFormat("#,##0.##").format(number)

                Text(
                    text = "Result: $formatted",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        item {
            if (state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
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


@Composable
fun CurrencyDropdown(
    label: String,
    currencies: List<Currency>,
    selected: Currency?,
    exclude: String? = null,
    onSelect: (Currency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val filtered = currencies.filterNot { it.code == exclude }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected?.code ?: "Select")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                filtered.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text("${currency.code} - ${currency.name}") },
                        onClick = {
                            expanded = false
                            onSelect(currency)
                        }
                    )
                }
            }
        }
    }
}