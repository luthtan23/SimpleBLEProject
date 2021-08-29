package com.luthtan.simplebleproject.data.network.datasource

import androidx.lifecycle.MutableLiveData
import com.luthtan.simplebleproject.data.BuildConfig
import com.luthtan.simplebleproject.data.network.ApiResponse
import com.luthtan.simplebleproject.data.network.ApiService
import com.luthtan.simplebleproject.domain.response.dashboard.BleResponse
import kotlinx.coroutines.*
import java.lang.Exception

class RemoteDataSource(private val apiService: ApiService) {

    fun getUserData(): MutableLiveData<ApiResponse<BleResponse>> {
        val bleResponse = MutableLiveData<ApiResponse<BleResponse>>()
        GlobalScope.launch {
            try {
                val request = apiService.getBleUser()
                if (request.isSuccessful) {
                    if (request.body() != null) {
                        bleResponse.postValue(ApiResponse.success(request.body()!!))
                    } else {
                        bleResponse.postValue(ApiResponse.empty(request.message(), request.body()!!))
                    }
                } else {
                    bleResponse.postValue(ApiResponse.error(request.message()))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                bleResponse.postValue(ApiResponse.error(e.toString()))
            }
        }
        return bleResponse
    }
}