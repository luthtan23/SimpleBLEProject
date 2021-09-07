package com.luthtan.simplebleproject.data.di

import com.luthtan.simplebleproject.data.datasource.LocalDataSource
import com.luthtan.simplebleproject.data.datasource.RemoteDataSource
import com.luthtan.simplebleproject.data.utils.AppExecutors
import org.koin.dsl.module

val remoteModule = module {

    single { RemoteDataSource(get()) }
    single { LocalDataSource(get()) }
    single { AppExecutors() }

}