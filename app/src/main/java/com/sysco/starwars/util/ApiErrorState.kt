package com.sysco.starwars.util

import android.util.Log
import kotlinx.coroutines.TimeoutCancellationException
import java.io.IOException

sealed class ApiErrorState(message: String) : Exception(message) {

    class NoNetworkErrorState(message: String = "No internet connection") : ApiErrorState(message)
    class DataSourceErrorState(message: String) : ApiErrorState(message)
    class TimeOutErrorState(message: String) : ApiErrorState(message)

    companion object {
        fun from(e: Exception): ApiErrorState {
            Log.d(ApiErrorState::class.java.simpleName, "ApiErrorState ----> ${e.message.toString()}")
            return when (e) {
                is IOException -> NoNetworkErrorState()
                is TimeoutCancellationException -> TimeOutErrorState("Timeout")
                else -> DataSourceErrorState(e.message ?: "Unknown error occurred...")
            }
        }
    }
}