package com.example.helioflow.api

import android.content.Context
import com.example.helioflow.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object ShutterApiClient {

    private const val BASE_URL = "https://helioflow.alwaysdata.net/"

    private var tokenSupplier: (() -> String?)? = null
    private var okHttpClient: OkHttpClient? = null

    fun initialize(context: Context) {
        val tokenManager = TokenManager(context)
        tokenSupplier = { tokenManager.getToken() }
        val supplier = tokenSupplier
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(supplier!!))
            .build()
    }

    val instance: ShutterApiService by lazy {
        if (okHttpClient == null) {
            throw IllegalStateException("ShutterApiClient must be initialized with context first")
        }
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient!!)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(ShutterApiService::class.java)
    }
}