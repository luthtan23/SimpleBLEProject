package com.luthtan.simplebleproject.data.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.luthtan.simplebleproject.data.local.room.DashboardDao
import com.luthtan.simplebleproject.domain.entities.dashboard.BleEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocalDataSource(private val dashboardDao: DashboardDao) {

    fun getAllUserData(): LiveData<List<BleEntity>> = dashboardDao.getAllUserData()

    suspend fun insertUserData(bleEntity: BleEntity) = dashboardDao.insertUserData(bleEntity)
}