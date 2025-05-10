package com.amolina.worldcurrency.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolina.worldcurrency.domain.model.Conversion
import com.amolina.worldcurrency.domain.usecase.GetConversionHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getConversionHistory: GetConversionHistoryUseCase,
) : ViewModel() {

    private val _history = MutableStateFlow<List<Conversion>>(emptyList())
    val history: StateFlow<List<Conversion>> = _history

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _history.value = getConversionHistory()
        }
    }
}