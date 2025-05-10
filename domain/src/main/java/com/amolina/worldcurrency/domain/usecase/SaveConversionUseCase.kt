package com.amolina.worldcurrency.domain.usecase

import com.amolina.worldcurrency.domain.model.Conversion
import com.amolina.worldcurrency.domain.repository.CurrencyRepository

class SaveConversionUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(conversion: Conversion) {
        repository.saveConversion(conversion)
    }
}
