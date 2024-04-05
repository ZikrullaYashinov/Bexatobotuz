package com.apextech.bexatobotuz.data.remote.api

import com.apextech.bexatobotuz.data.remote.response.WordResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("dictionary-cyrills")
    suspend fun getCyrills(): Response<List<WordResponse>>

    @GET("dictionary-latins")
    suspend fun getLatins(): Response<List<WordResponse>>
}