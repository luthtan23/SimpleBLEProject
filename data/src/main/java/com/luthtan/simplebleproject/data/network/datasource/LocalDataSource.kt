package com.luthtan.simplebleproject.data.network.datasource

import androidx.lifecycle.LiveData
import com.luthtan.simplebleproject.data.local.room.DashboardDao
import com.luthtan.simplebleproject.domain.entities.dashboard.BleEntity

class LocalDataSource(private val dashboardDao: DashboardDao) {

    fun getUserData(): LiveData<BleEntity> = dashboardDao.getUserData()

    fun getAllUserData(): LiveData<List<BleEntity>> = dashboardDao.getAllUserData()

    fun insertUserData(bleEntity: BleEntity) = dashboardDao.insertUserData(bleEntity)
}