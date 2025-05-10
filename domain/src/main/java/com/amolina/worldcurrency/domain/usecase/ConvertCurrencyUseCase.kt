package com.amolina.worldcurrency.domain.usecase

import com.amolina.worldcurrency.domain.repository.CurrencyRepository

class ConvertCurrencyUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(amount: Double, from: String, to: String): Double =
        repository.convert(amount, from, to)
}
