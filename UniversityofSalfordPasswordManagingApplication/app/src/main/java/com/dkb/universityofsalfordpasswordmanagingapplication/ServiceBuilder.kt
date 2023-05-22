package com.dkb.universityofsalfordpasswordmanagingapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private const val BASE_URL: String = "https://10.0.2.2:8000/"
    // Use when signed with trusted source
    //private val client = OkHttpClient.Builder().build()

    private val client = UnsafeOkHttpClient.getUnsafeOkHttpClient()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // change this IP for testing by your actual machine IP
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}