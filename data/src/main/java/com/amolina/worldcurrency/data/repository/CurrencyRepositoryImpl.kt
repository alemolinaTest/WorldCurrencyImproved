package com.amolina.worldcurrency.data.repository

import android.util.Log
import com.amolina.worldcurrency.data.di.IoDispatcher
import com.amolina.worldcurrency.data.local.room.dao.CurrencyDao
import com.amolina.worldcurrency.data.local.room.entity.CurrencyRateEntity
import com.amolina.worldcurrency.data.remote.api.ApiService
import com.amolina.worldcurrency.domain.model.Currency
import com.amolina.worldcurrency.domain.repository.CurrencyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CurrencyRepositoryImpl(
    private val api: ApiService,
    private val dao: CurrencyDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CurrencyRepository {

    override suspend fun getAvailableCurrencies(): List<Currency> = withContext(ioDispatcher) {
        runCatching {
            Log.d("CurrencyRepo", "Fetching currencies and rates...")

            // Fetch currencies and latest rates
            val currenciesResponse = api.getCurrencies() // /list
            val latestRates = api.getLatestRates()     // /live

            val base = latestRates.source // should be "USD"

            val entities = latestRates.quotes.mapNotNull { (pairCode, rate) ->
                // Extract currency code by removing the "USD" prefix
                if (pairCode.startsWith(base)) {
                    val code = pairCode.removePrefix(base)
                    val name = currenciesResponse.currencies[code] ?: return@mapNotNull null
                    CurrencyRateEntity(code = code, name = name, rate = rate)
                } else null
            }

            dao.insertRates(entities)
        }.onFailure {
            Log.e("CurrencyRepo", "Failed to fetch remote data", it)
        }

        return@withContext dao.getAllRates().map { it.toDomain() }
    }

    override suspend fun convert(amount: Double, from: String, to: String): Double =
        withContext(ioDispatcher) {
            val localRates = dao.getAllRates()
            val fromRate = localRates.find { it.code == from }?.rate ?: 1.0
            val toRate = localRates.find { it.code == to }?.rate ?: 1.0

            runCatching {
                val currencies = api.getCurrencies().currencies
                val rates = api.getLatestRates()

                val base = rates.source

                val entities = rates.quotes.mapNotNull { (pairCode, rate) ->
                    if (pairCode.startsWith(base)) {
                        val code = pairCode.removePrefix(base)
                        val name = currencies[code] ?: return@mapNotNull null
                        CurrencyRateEntity(code = code, name = name, rate = rate)
                    } else null
                }

                dao.insertRates(entities)
            }

            return@withContext (amount / fromRate) * toRate
        }

    private fun CurrencyRateEntity.toDomain(): Currency =
        Currency(code = code, name = name, rate = rate)
}