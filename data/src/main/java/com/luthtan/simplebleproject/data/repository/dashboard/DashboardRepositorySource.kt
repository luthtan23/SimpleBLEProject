package com.luthtan.simplebleproject.data.repository.dashboard

import androidx.lifecycle.LiveData
import com.luthtan.simplebleproject.data.network.ApiResponse
import com.luthtan.simplebleproject.domain.response.dashboard.BleResponse

interface DashboardRepositorySource {

    fun getUserData(): LiveData<ApiResponse<BleResponse>>

}