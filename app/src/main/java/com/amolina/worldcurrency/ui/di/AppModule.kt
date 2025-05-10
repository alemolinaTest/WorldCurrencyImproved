package com.amolina.worldcurrency.ui.di

import com.amolina.worldcurrency.data.remote.api.ApiService
import com.amolina.worldcurrency.domain.repository.CurrencyRepository
import com.amolina.worldcurrency.domain.usecase.ConvertCurrencyUseCase
import com.amolina.worldcurrency.domain.usecase.GetAvailableCurrenciesUseCase
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@OptIn(ExperimentalSerializationApi::class)
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.exchangerate.host/"
    private const val API_KEY = "05271ae76c6a42c960ed024920d5a818" // ✅ Fixed newline

    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor = Interceptor { chain ->
        val original = chain.request()
        val newUrl = original.url.newBuilder()
            .addQueryParameter("access_key", API_KEY)
            .build()
        val newRequest = original.newBuilder().url(newUrl).build()
        chain.proceed(newRequest)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    fun provideGetAvailableCurrenciesUseCase(repository: CurrencyRepository): GetAvailableCurrenciesUseCase =
        GetAvailableCurrenciesUseCase(repository)

    @Provides
    fun provideConvertCurrencyUseCase(repository: CurrencyRepository): ConvertCurrencyUseCase =
        ConvertCurrencyUseCase(repository)
}