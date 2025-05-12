package com.amolina.worldcurrency.data.repository

import android.util.Log
import com.amolina.worldcurrency.data.di.IoDispatcher
import com.amolina.worldcurrency.data.local.room.dao.ConversionDao
import com.amolina.worldcurrency.data.local.room.dao.CurrencyDao
import com.amolina.worldcurrency.data.local.room.entity.CurrencyRateEntity
import com.amolina.worldcurrency.data.local.room.entity.toDomain
import com.amolina.worldcurrency.data.local.room.entity.toEntity
import com.amolina.worldcurrency.data.remote.api.ApiService
import com.amolina.worldcurrency.domain.model.Conversion
import com.amolina.worldcurrency.domain.model.Currency
import com.amolina.worldcurrency.domain.repository.CurrencyRepository
import com.amolina.worldcurrency.domain.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CurrencyRepositoryImpl(
    private val api: ApiService,
    private val dao: CurrencyDao,
    private val conversionDao: ConversionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CurrencyRepository {

    override suspend fun getAvailableCurrencies(): Resource<List<Currency>> =
        withContext(ioDispatcher) {
            val result = fetchAndCacheLatestRates()

            val localCurrencies = dao.getAllRates().map { it.toDomain() }

            return@withContext if (result.isSuccess) {
                Resource.Success(localCurrencies, isFromCache = false)
            } else {
                result.exceptionOrNull()?.let {
                    Log.e("CurrencyRepo", "Fallo al actualizar tasas", it)
                }
                Resource.Success(localCurrencies, isFromCache = true)
            }
        }

    override suspend fun convert(amount: Double, from: String, to: String): Resource<Double> =
        withContext(ioDispatcher) {
            try {
                require(amount > 0) { "El monto debe ser mayor a cero" }
                require(from.isNotBlank() && to.isNotBlank()) { "Los códigos no pueden estar vacíos" }
                require(from != to) { "Las monedas deben ser distintas" }

                val localRates = dao.getAllRates()
                val fromRate = localRates.find { it.code == from }?.rate
                    ?: return@withContext Resource.Error(
                        IllegalArgumentException("Moneda origen no encontrada"),
                        isFromCache = false
                    )

                val toRate = localRates.find { it.code == to }?.rate
                    ?: return@withContext Resource.Error(
                        IllegalArgumentException("Moneda destino no encontrada"),
                        isFromCache = false
                    )

                val result = fetchAndCacheLatestRates()
                val finalResult = (amount / fromRate) * toRate

                Resource.Success(finalResult, isFromCache = result.isFailure)

            } catch (e: Exception) {
                Log.e("CurrencyRepo", "Error en conversión", e)
                Resource.Error(e)
            }
        }

    override suspend fun saveConversion(entity: Conversion) =
        conversionDao.insert(entity.toEntity())

    override suspend fun getConversionHistory(): List<Conversion> =
        conversionDao.getAll().map { it.toDomain() }

    override suspend fun getConversionById(id: Long): Conversion =
        conversionDao.getById(id).toDomain()

    private suspend fun fetchAndCacheLatestRates(): Result<Unit> = runCatching {
        Log.d("CurrencyRepo", "Fetching currencies and rates...")

        val currenciesResponse = api.getCurrencies() // /list → devuelve Map<String, String>
        val latestRates = api.getLatestRates() // /live → devuelve source y quotes

        val base = latestRates.source ?: "USD"
        val quotes = latestRates.quotes
            ?: throw IllegalStateException("La API devolvió quotes vacíos")


        val entities = quotes.mapNotNull { (pairCode, rate) ->
            if (pairCode.startsWith(base)) {
                val code = pairCode.removePrefix(base)
                val name = currenciesResponse.currencies[code] ?: return@mapNotNull null
                CurrencyRateEntity(code = code, name = name, rate = rate)
            } else null
        }

        dao.insertRates(entities)
    }.onFailure {
        Log.e("CurrencyRepo", "Error al obtener datos remotos", it)
    }

}