package com.udacity.core

import android.app.Application
import com.udacity.core.connectionchecker.ConnectionCheckerModule
import com.udacity.di.initDependencies
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LoadingStatusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@LoadingStatusApplication)
        }
        initDependencies()
        ConnectionCheckerModule.load()
    }
}