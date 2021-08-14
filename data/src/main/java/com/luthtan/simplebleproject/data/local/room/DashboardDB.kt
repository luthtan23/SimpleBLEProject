package com.luthtan.simplebleproject.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.luthtan.simplebleproject.domain.entities.dashboard.BleEntity

@Database(
    entities = [BleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DashboardDB : RoomDatabase() {

    abstract fun dashboardDao(): DashboardDao
}