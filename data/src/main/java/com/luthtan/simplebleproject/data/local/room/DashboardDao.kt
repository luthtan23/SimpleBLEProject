package com.luthtan.simplebleproject.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.luthtan.simplebleproject.domain.entities.dashboard.BleEntity

@Dao
interface DashboardDao {

    @Query("SELECT * from user_ble_table ORDER BY id DESC")
    fun getAllUserData(): LiveData<List<BleEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserData(bleEntity: BleEntity)
}