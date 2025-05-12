package com.amolina.worldcurrency.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolina.worldcurrency.domain.model.Conversion
import com.amolina.worldcurrency.domain.usecase.ConvertCurrencyUseCase
import com.amolina.worldcurrency.domain.usecase.GetAvailableCurrenciesUseCase
import com.amolina.worldcurrency.domain.usecase.SaveConversionUseCase
import com.amolina.worldcurrency.domain.util.Resource
import com.amolina.worldcurrency.presentation.ui.event.CurrencyUiEvent
import com.amolina.worldcurrency.presentation.ui.mapper.ErrorMapper.toCurrencyUiError
import com.amolina.worldcurrency.presentation.ui.state.CurrencyUiData
import com.amolina.worldcurrency.presentation.ui.state.CurrencyUiState
import com.amolina.worldcurrency.presentation.ui.state.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val getCurrencies: GetAvailableCurrenciesUseCase,
    private val convertCurrency: ConvertCurrencyUseCase,
    private val saveConversion: SaveConversionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Loading)
    val state: StateFlow<CurrencyUiState> = _state

    init {
        loadCurrencies()
    }

    fun onEvent(event: CurrencyUiEvent) {
        val current = (_state.value as? CurrencyUiState.Success)?.data

        when (event) {
            is CurrencyUiEvent.OnAmountChanged -> {
                if (current != null) {
                    updateState(current.copy(amount = event.value))
                }
            }

            is CurrencyUiEvent.OnFromCurrencySelected -> {
                val selected = current?.availableCurrencies?.find { it.code == event.code }
                if (current != null && selected != null) {
                    updateState(current.copy(selectedFrom = selected))
                }
            }

            is CurrencyUiEvent.OnToCurrencySelected -> {
                val selected = current?.availableCurrencies?.find { it.code == event.code }
                if (current != null && selected != null) {
                    updateState(current.copy(selectedTo = selected))
                }
            }

            CurrencyUiEvent.OnConvert -> {
                if (current != null) convert(current)
            }
        }
    }

    private fun loadCurrencies() {
        _state.value = CurrencyUiState.Loading
        viewModelScope.launch {
            when (val result = getCurrencies()) {
                is Resource.Success -> {
                    val data = CurrencyUiData(
                        availableCurrencies = result.data,
                        isFromCache = result.isFromCache
                    )
                    _state.value = CurrencyUiState.Success(data)
                }

                is Resource.Error -> {
                    _state.value = toCurrencyUiError(result.exception)
                }

                Resource.Loading -> {
                    _state.value = CurrencyUiState.Loading
                }
            }
        }
    }

    private fun convert(current: CurrencyUiData) {
        viewModelScope.launch {
            val amount = current.amount.toDoubleOrNull()
            if (amount == null) {
                _state.value = CurrencyUiState.Error("Invalid amount", ErrorType.VALIDATION)
                return@launch
            }

            if (current.selectedFrom == null || current.selectedTo == null) {
                _state.value = CurrencyUiState.Error("Select both currencies", ErrorType.VALIDATION)
                return@launch
            }

            _state.value = CurrencyUiState.Loading

            val result = convertCurrency(amount, current.selectedFrom.code, current.selectedTo.code)

            when (result) {
                is Resource.Success -> {
                    val value = result.data
                    if (value is Double) {
                        val rate = value / amount

                        saveConversion(
                            Conversion(
                                fromCode = current.selectedFrom.code,
                                fromName = current.selectedFrom.name,
                                toCode = current.selectedTo.code,
                                toName = current.selectedTo.name,
                                amount = amount,
                                rate = rate,
                                result = value,
                                timestamp = System.currentTimeMillis()
                            )
                        )

                        updateState(
                            current.copy(
                                result = "%.2f".format(value),
                                isFromCache = result.isFromCache
                            )
                        )
                    } else {
                        _state.value = CurrencyUiState.Error("Unexpected result type", ErrorType.GENERIC)
                    }
                }

                is Resource.Error -> {
                    _state.value = toCurrencyUiError(result.exception)
                }

                Resource.Loading -> {
                    _state.value = CurrencyUiState.Loading
                }
            }
        }
    }


    private fun updateState(data: CurrencyUiData) {
        _state.value = CurrencyUiState.Success(data)
    }
}
