package com.udacity.di

import com.udacity.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

internal val mainModule = module {
    viewModel {
        MainViewModel(
            connectionChecker = get()
        )
    }
}

internal val mainDependencies by lazy {
    loadKoinModules(mainModule)
}

fun initDependencies() = mainDependencies