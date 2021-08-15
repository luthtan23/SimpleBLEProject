package com.luthtan.simplebleproject.data.repository.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.luthtan.simplebleproject.data.network.ApiResponse
import com.luthtan.simplebleproject.data.network.datasource.LocalDataSource
import com.luthtan.simplebleproject.data.network.datasource.RemoteDataSource
import com.luthtan.simplebleproject.data.utils.AppExecutors
import com.luthtan.simplebleproject.domain.entities.dashboard.BleEntity
import com.luthtan.simplebleproject.domain.response.dashboard.BleResponse

class DashboardRepository (
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val appExecutors: AppExecutors
) : DashboardRepositorySource{

    override fun getUserData(): LiveData<ApiResponse<BleResponse>> = remoteDataSource.getUserData()

    override suspend fun insertUserData(bleEntity: BleEntity) = localDataSource.insertUserData(bleEntity)

    override fun getAllUserData(): LiveData<List<BleEntity>> = localDataSource.getAllUserData()
}