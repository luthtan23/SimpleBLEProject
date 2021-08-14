package com.luthtan.simplebleproject.data.di

import androidx.room.Room
import com.luthtan.simplebleproject.data.local.room.DashboardDB
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            (androidApplication()),
            DashboardDB::class.java,
            "dashboard_db"
        )
            .build()
    }

    single { (get() as DashboardDB).dashboardDao() }
}