package com.amolina.worldcurrency.data.remote.api

import com.amolina.worldcurrency.data.model.CurrenciesResponseDto
import com.amolina.worldcurrency.data.model.LatestRatesResponseDto
import retrofit2.http.GET

interface ApiService {

    @GET("list")
    suspend fun getCurrencies(): CurrenciesResponseDto

    @GET("live")
    suspend fun getLatestRates(): LatestRatesResponseDto
}