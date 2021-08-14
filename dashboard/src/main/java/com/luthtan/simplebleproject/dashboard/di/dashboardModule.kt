package com.luthtan.simplebleproject.dashboard.di

import com.luthtan.simplebleproject.dashboard.viewmodel.DashboardViewModel
import org.koin.dsl.module

val dashboardModule = module {

    single { DashboardViewModel(get()) }
}