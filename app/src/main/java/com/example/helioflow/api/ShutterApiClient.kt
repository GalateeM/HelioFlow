package com.example.helioflow.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ShutterApiClient {

    private const val BASE_URL = "https://monapp.alwaysdata.net/"

    private const val TOKEN = "un_truc_long_et_imprevisible_123456"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(TOKEN))
        .build()

    val instance: ShutterApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShutterApiService::class.java)
    }
}