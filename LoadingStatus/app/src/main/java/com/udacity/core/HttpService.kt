package com.udacity.core

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = setupRetrofit()

fun setupRetrofit() : Retrofit {
    val httpClient = OkHttpClient.Builder()

    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    httpClient.callTimeout(timeout = 20L, unit = TimeUnit.SECONDS)
    httpClient.addInterceptor(httpLoggingInterceptor)

    return Retrofit.Builder()
        .baseUrl(BuildConfig.DOWNLOAD_URL)
        .client(httpClient.build())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}

class ApiService {
    companion object {
        fun <T> createService(service: Class<T>) : T {
            return retrofit.create(service)
        }
    }
}
