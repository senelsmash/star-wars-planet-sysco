package com.sysco.starwars.util

import java.io.IOException

sealed class ApiErrorState(message: String) : Exception(message) {

    class NoNetworkErrorState(message: String = "No internet connection") : ApiErrorState(message)
    class DataSourceErrorState(message: String) : ApiErrorState(message)

    companion object {
        fun from(e: Exception): ApiErrorState {
            return when (e) {
                is IOException -> NoNetworkErrorState()
                else -> DataSourceErrorState(e.message ?: "Unknown error occurred...")
            }
        }
    }
}