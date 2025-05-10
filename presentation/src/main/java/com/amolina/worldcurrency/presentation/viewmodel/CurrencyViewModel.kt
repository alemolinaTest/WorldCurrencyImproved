package com.amolina.worldcurrency.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolina.worldcurrency.domain.model.Conversion
import com.amolina.worldcurrency.domain.usecase.ConvertCurrencyUseCase
import com.amolina.worldcurrency.domain.usecase.GetAvailableCurrenciesUseCase
import com.amolina.worldcurrency.domain.usecase.SaveConversionUseCase
import com.amolina.worldcurrency.presentation.ui.event.CurrencyUiEvent
import com.amolina.worldcurrency.presentation.ui.state.CurrencyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val getCurrencies: GetAvailableCurrenciesUseCase,
    private val convertCurrency: ConvertCurrencyUseCase,
    private val saveConversion: SaveConversionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CurrencyUiState())
    val state: StateFlow<CurrencyUiState> = _state

    init {
        loadCurrencies()
    }

    fun onEvent(event: CurrencyUiEvent) {
        when (event) {
            is CurrencyUiEvent.OnAmountChanged ->
                _state.update { it.copy(amount = event.value, error = null) }

            is CurrencyUiEvent.OnFromCurrencySelected ->
                _state.update {
                    val currency = it.availableCurrencies.find { c -> c.code == event.code }
                    it.copy(selectedFrom = currency, error = null)
                }

            is CurrencyUiEvent.OnToCurrencySelected ->
                _state.update {
                    val currency = it.availableCurrencies.find { c -> c.code == event.code }
                    it.copy(selectedTo = currency, error = null)
                }

            CurrencyUiEvent.OnConvert -> convert()
        }
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val currencies = getCurrencies()
                _state.update { it.copy(isLoading = false, availableCurrencies = currencies) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    private fun convert() {
        viewModelScope.launch {
            val current = _state.value

            if (current.selectedFrom == null || current.selectedTo == null) {
                _state.update { it.copy(error = "Select both currencies") }
                return@launch
            }

            val amount = current.amount.toDoubleOrNull()
            if (amount == null) {
                _state.update { it.copy(error = "Invalid amount") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val result = convertCurrency(amount, current.selectedFrom.code, current.selectedTo.code)

                val rate = result / amount
                saveConversion(
                    Conversion(
                        fromCode = current.selectedFrom.code,
                        fromName = current.selectedFrom.name,
                        toCode = current.selectedTo.code,
                        toName = current.selectedTo.name,
                        amount = amount,
                        rate = rate,
                        result = result,
                        timestamp = System.currentTimeMillis()
                    )
                )

                _state.update {
                    it.copy(result = "%.2f".format(result), isLoading = false)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message, isLoading = false)
                }
            }
        }
    }
}
