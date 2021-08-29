package com.luthtan.simplebleproject

import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.luthtan.simplebleproject.data.di.coreModule
import com.luthtan.simplebleproject.data.di.databaseModule
import com.luthtan.simplebleproject.data.di.remoteModule
import com.luthtan.simplebleproject.data.di.repoModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BleProjectApplication : SplitCompatApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BleProjectApplication)
            modules(listOf(coreModule, databaseModule, repoModule, remoteModule))
        }
    }
}