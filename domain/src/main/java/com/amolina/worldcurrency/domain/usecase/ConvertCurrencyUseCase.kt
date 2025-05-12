package com.amolina.worldcurrency.domain.usecase

import com.amolina.worldcurrency.domain.repository.CurrencyRepository
import com.amolina.worldcurrency.domain.util.Resource

class ConvertCurrencyUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(amount: Double, from: String, to: String): Resource<Double> =
        repository.convert(amount, from, to)
}
