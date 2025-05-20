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
            try {
                val result = fetchAndCacheLatestRates()
                val localCurrencies = dao.getAllRates().map { it.toDomain() }

                Resource.Success(localCurrencies, isFromCache = result.isFailure)
            } catch (e: Exception) {
                Log.e("CurrencyRepo", "Fallo al actualizar tasas", e)
                val message = mapErrorToMessage(e)
                Resource.Error(Exception(message), isFromCache = true)
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
                    ?: throw IllegalArgumentException("Moneda origen no encontrada")
                val toRate = localRates.find { it.code == to }?.rate
                    ?: throw IllegalArgumentException("Moneda destino no encontrada")

                val result = fetchAndCacheLatestRates()
                val finalResult = (amount / fromRate) * toRate

                Resource.Success(finalResult, isFromCache = result.isFailure)

            } catch (e: Exception) {
                Log.e("CurrencyRepo", "Error en conversión", e)
                val message = mapErrorToMessage(e)
                Resource.Error(Exception(message))
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

        val currenciesResponse = api.getCurrencies()
        if (!currenciesResponse.success) {
            val msg = currenciesResponse.error?.info ?: "Error desconocido al obtener monedas"
            throw ApiException(msg)
        }
        val currenciesMap = currenciesResponse.currencies
            ?: throw ApiException("No se recibieron monedas desde la API")

        val latestRates = api.getLatestRates()
        if (!latestRates.success) {
            val msg = latestRates.error?.info ?: "Error desconocido al obtener tasas"
            throw ApiException(msg)
        }
        val base = latestRates.source ?: "USD"
        val quotes = latestRates.quotes
            ?: throw ApiException("No se recibieron tasas desde la API")

        val entities = quotes.mapNotNull { (pairCode, rate) ->
            if (pairCode.startsWith(base)) {
                val code = pairCode.removePrefix(base)
                val name = currenciesMap[code] ?: return@mapNotNull null
                CurrencyRateEntity(code = code, name = name, rate = rate)
            } else null
        }

        dao.insertRates(entities)
    }.onFailure {
        Log.e("CurrencyRepo", "Error al obtener datos remotos", it)
    }

    private fun mapErrorToMessage(e: Exception): String = when (e) {
        is ApiException -> e.message ?: "Error de API"
        is java.net.UnknownHostException -> "Sin conexión a Internet"
        is kotlinx.serialization.SerializationException -> "Error al interpretar la respuesta del servidor"
        is IllegalArgumentException -> e.message ?: "Datos inválidos"
        else -> "Error inesperado: ${e.localizedMessage}"
    }
}

class ApiException(message: String) : Exception(message)

