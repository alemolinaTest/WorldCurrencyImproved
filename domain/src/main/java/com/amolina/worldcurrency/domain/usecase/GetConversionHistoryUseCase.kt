package com.amolina.worldcurrency.domain.usecase

import com.amolina.worldcurrency.domain.model.Conversion
import com.amolina.worldcurrency.domain.repository.CurrencyRepository

class GetConversionHistoryUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(): List<Conversion> = repository.getConversionHistory()
}
