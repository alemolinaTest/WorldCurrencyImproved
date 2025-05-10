package com.amolina.worldcurrency.domain.usecase

import com.amolina.worldcurrency.domain.model.Currency
import com.amolina.worldcurrency.domain.repository.CurrencyRepository

class GetAvailableCurrenciesUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(): List<Currency> = repository.getAvailableCurrencies()
}