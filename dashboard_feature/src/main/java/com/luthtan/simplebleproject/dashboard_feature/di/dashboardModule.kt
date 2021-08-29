package com.luthtan.simplebleproject.dashboard_feature.di

import com.luthtan.simplebleproject.dashboard_feature.viewmodel.DashboardViewModel
import org.koin.dsl.module

val dashboardModule = module {

    single { DashboardViewModel(get()) }
}