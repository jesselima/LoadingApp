package com.udacity.data

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

interface AppDownloadService {

    @Streaming
    @GET
    suspend fun getData(): ResponseBody?
}