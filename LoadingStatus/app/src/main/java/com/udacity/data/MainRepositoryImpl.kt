package com.udacity.data

import okhttp3.ResponseBody

internal class MainRepositoryImpl(
    private val appDownloadService: AppDownloadService
) : MainRepository {

    override suspend fun getData(): ResponseBody? {
        return appDownloadService.getData()
    }
}
