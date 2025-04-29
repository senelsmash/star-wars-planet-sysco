package com.sysco.starwars.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface PicsumApi {

    @GET("200")
    suspend fun getRedirectUrl(): Response<ResponseBody>
}