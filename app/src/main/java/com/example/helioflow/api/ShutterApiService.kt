package com.example.helioflow.api

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

data class Programmation(
    val id: Int,
    val action: String,
    val execution_time: String,
)

interface ShutterApiService {

    @GET("programmations")
    suspend fun getProgrammations(): List<Programmation>

}