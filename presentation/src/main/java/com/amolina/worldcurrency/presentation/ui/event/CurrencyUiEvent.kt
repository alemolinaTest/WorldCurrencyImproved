package com.amolina.worldcurrency.presentation.ui.event

 sealed class CurrencyUiEvent {
    data class OnAmountChanged(val value: String) : CurrencyUiEvent()
    data class OnFromCurrencySelected(val code: String) : CurrencyUiEvent()
    data class OnToCurrencySelected(val code: String) : CurrencyUiEvent()
    object OnConvert : CurrencyUiEvent()
}