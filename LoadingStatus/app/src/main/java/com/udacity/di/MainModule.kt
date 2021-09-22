package com.udacity.di

import com.udacity.MainViewModel
import com.udacity.core.ApiService
import com.udacity.data.AppDownloadService
import com.udacity.data.MainRepository
import com.udacity.data.MainRepositoryImpl
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module


internal val mainModule = module {

    factory {
        ApiService.createService(AppDownloadService::class.java)
    }

    factory<MainRepository> {
        MainRepositoryImpl(appDownloadService = get() )
    }

    viewModel {
        MainViewModel(
            dispatchers = Dispatchers.IO,
            connectionChecker = get()
        )
    }
}

internal val mainDependencies by lazy {
    loadKoinModules(mainModule)
}

fun initDependencies() = mainDependencies