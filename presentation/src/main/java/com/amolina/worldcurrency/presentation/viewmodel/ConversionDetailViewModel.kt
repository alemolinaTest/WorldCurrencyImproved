package com.amolina.worldcurrency.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolina.worldcurrency.domain.model.Conversion
import com.amolina.worldcurrency.domain.usecase.GetConversionByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ConversionDetailViewModel @Inject constructor(
    private val getConversionById: GetConversionByIdUseCase
) : ViewModel() {

    private val _conversion = MutableStateFlow<Conversion?>(null)
    val conversion: StateFlow<Conversion?> = _conversion

    fun load(id: Long) {
        viewModelScope.launch {
            _conversion.value = getConversionById(id)
        }
    }
}
