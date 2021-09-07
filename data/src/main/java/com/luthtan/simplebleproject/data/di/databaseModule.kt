package com.luthtan.simplebleproject.data.di

import androidx.room.Room
import com.luthtan.simplebleproject.data.local.room.DashboardDB
import com.luthtan.simplebleproject.data.utils.getDateFromMillis
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.io.File

const val DATABASE_NAME = "dashboard_db"

val databaseModule = module {

    single {
        Room.databaseBuilder(
            (androidApplication()),
            DashboardDB::class.java,
            DATABASE_NAME
        )
            .build()
    }

    single { (get() as DashboardDB).dashboardDao() }

}