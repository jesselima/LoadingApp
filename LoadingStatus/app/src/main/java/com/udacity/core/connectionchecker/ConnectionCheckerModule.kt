package com.udacity.core.connectionchecker

import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

object ConnectionCheckerModule {
    private val connectionCheckerModule = module {
        single<ConnectionChecker> {
            ConnectionCheckerImpl(context = get())
        }
    }
    fun load() = loadKoinModules(connectionCheckerModule)
}