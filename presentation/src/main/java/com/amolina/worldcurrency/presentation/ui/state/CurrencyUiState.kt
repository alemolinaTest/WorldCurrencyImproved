package com.amolina.worldcurrency.presentation.ui.state

import com.amolina.worldcurrency.domain.model.Currency

data class CurrencyUiState(
    val isLoading: Boolean = false,
    val availableCurrencies: List<Currency> = emptyList(),
    val selectedFrom: Currency? = null,
    val selectedTo: Currency? = null,
    val amount: String = "",
    val result: String = "",
    val error: String? = null
)