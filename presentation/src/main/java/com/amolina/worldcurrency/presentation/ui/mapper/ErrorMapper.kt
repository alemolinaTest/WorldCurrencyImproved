package com.amolina.worldcurrency.presentation.ui.mapper

import com.amolina.worldcurrency.presentation.ui.state.CurrencyUiState
import com.amolina.worldcurrency.presentation.ui.state.ErrorType
import java.io.IOException
import retrofit2.HttpException

object ErrorMapper {

    fun toCurrencyUiError(e: Throwable): CurrencyUiState.Error {
        val type = when (e) {
            is IOException -> ErrorType.NETWORK
            is HttpException -> ErrorType.SERVER
            is IllegalArgumentException -> ErrorType.VALIDATION
            else -> ErrorType.GENERIC
        }

        return CurrencyUiState.Error(
            message = e.message ?: "Unexpected error",
            type = type
        )
    }
}
