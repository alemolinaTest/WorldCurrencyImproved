package com.amolina.worldcurrency.domain.usecase

import com.amolina.worldcurrency.domain.model.Conversion
import com.amolina.worldcurrency.domain.repository.CurrencyRepository

class GetConversionByIdUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(id: Long): Conversion {
        return repository.getConversionById(id)
    }
}
