package com.amolina.worldcurrency.data.di

import android.content.Context
import androidx.room.Room
import com.amolina.worldcurrency.data.local.room.dao.CurrencyDao
import com.amolina.worldcurrency.data.local.room.db.CurrencyDatabase
import com.amolina.worldcurrency.data.remote.api.ApiService
import com.amolina.worldcurrency.data.repository.CurrencyRepositoryImpl
import com.amolina.worldcurrency.domain.repository.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): CurrencyDatabase =
        Room.databaseBuilder(
            appContext,
            CurrencyDatabase::class.java,
            "currency_db"
        ).build()

    @Provides
    fun provideCurrencyDao(db: CurrencyDatabase): CurrencyDao =
        db.currencyDao()

    @Provides
    @Singleton
    fun provideCurrencyRepository(
        api: ApiService,
        dao: CurrencyDao,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): CurrencyRepository =
        CurrencyRepositoryImpl(api, dao, dispatcher)
}