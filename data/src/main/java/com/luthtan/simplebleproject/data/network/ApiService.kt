package com.luthtan.simplebleproject.data.network

import com.luthtan.simplebleproject.domain.response.dashboard.BleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET(ApiConstant.DASHBOARD_URL)
    suspend fun getBleUser(): Response<BleResponse>

}