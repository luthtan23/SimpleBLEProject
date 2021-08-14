package com.luthtan.simplebleproject.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.luthtan.simplebleproject.domain.entities.dashboard.BleEntity

@Dao
interface DashboardDao {

    @Query("SELECT * from movie_db")
    fun getAllUserData(): LiveData<List<BleEntity>>

    @Query("SELECT * from movie_db")
    fun getUserData(): LiveData<BleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(bleEntity: BleEntity)
}