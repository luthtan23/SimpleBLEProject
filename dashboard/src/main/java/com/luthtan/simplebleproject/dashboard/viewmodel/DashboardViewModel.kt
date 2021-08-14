package com.luthtan.simplebleproject.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luthtan.simplebleproject.data.network.ApiResponse
import com.luthtan.simplebleproject.data.repository.dashboard.DashboardRepository
import com.luthtan.simplebleproject.domain.response.dashboard.BleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent

class DashboardViewModel(private val dashboardRepository: DashboardRepository) : ViewModel(), KoinComponent {

    fun getUserData(): LiveData<ApiResponse<BleResponse>> = dashboardRepository.getUserData()

}