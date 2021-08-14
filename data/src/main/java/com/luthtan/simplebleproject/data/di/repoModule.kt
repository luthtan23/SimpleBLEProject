package com.luthtan.simplebleproject.data.di

import com.luthtan.simplebleproject.data.repository.dashboard.DashboardRepository
import org.koin.dsl.module

val repoModule = module {

    single { DashboardRepository(get(), get(), get()) }

}