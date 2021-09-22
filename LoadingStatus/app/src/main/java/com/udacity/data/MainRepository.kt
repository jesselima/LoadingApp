package com.udacity.data

import okhttp3.ResponseBody

interface MainRepository {
    suspend fun getData(): ResponseBody?
}
