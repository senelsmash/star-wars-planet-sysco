package com.sysco.starwars.data.remote

import com.sysco.starwars.data.model.response.PlanetResponse
import com.sysco.starwars.util.ApiErrorState
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

interface StarWarsApiDataSource {

    suspend fun getPlanetList(): PlanetResponse

    suspend fun getPlanetPage(pageUrl: String): PlanetResponse

    suspend fun getPicsumImage(): String

}


class StarWarsApiDataSourceImpl @Inject constructor(
    private val starWarsApi: StarWarsApi,
    private val picsumApi: PicsumApi
) : StarWarsApiDataSource {

    override suspend fun getPlanetList(): PlanetResponse {
        try {
            return starWarsApi.getPlanetList()
        } catch (e: Exception) {
            throw ApiErrorState.from(e)
        }
    }

    override suspend fun getPlanetPage(pageUrl: String): PlanetResponse {
        try {
            return starWarsApi.getPlanetPage(pageUrl)
        } catch (e: Exception) {
            throw ApiErrorState.from(e)
        }
    }

    override suspend fun getPicsumImage(): String {
        return try {
            val response = picsumApi.getRedirectUrl()
            if (response.code() == 200) {
                response.raw().request.url.toUrl().toString()
            } else {
                throw ApiErrorState.DataSourceErrorState("Unexpected response code: ${response.code()}")
            }
        } catch (e: Exception) {
            throw ApiErrorState.from(e)
        }
    }



}