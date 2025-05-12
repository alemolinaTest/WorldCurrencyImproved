package com.amolina.worldcurrency.domain.usecase

import com.amolina.worldcurrency.domain.model.Currency
import com.amolina.worldcurrency.domain.repository.CurrencyRepository
import com.amolina.worldcurrency.domain.util.Resource

class GetAvailableCurrenciesUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(): Resource<List<Currency>> = repository.getAvailableCurrencies()
}