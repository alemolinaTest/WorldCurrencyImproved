package com.amolina.worldcurrency.presentation.ui.state

import com.amolina.worldcurrency.domain.model.Currency

sealed class CurrencyUiState {

    object Loading : CurrencyUiState()

    data class Success(
        val data: CurrencyUiData
    ) : CurrencyUiState()

    data class Error(val message: String, val type: ErrorType = ErrorType.GENERIC) : CurrencyUiState()
}

enum class ErrorType {
    NETWORK, SERVER, VALIDATION, GENERIC
}

data class CurrencyUiData(
    val availableCurrencies: List<Currency> = emptyList(),
    val selectedFrom: Currency? = null,
    val selectedTo: Currency? = null,
    val amount: String = "",
    val result: String = "",
    val isFromCache: Boolean = false
)