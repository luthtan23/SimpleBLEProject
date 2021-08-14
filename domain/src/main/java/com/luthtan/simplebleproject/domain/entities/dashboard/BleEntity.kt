package com.luthtan.simplebleproject.domain.entities.dashboard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_db")
data class BleEntity(
    @PrimaryKey
    val id: Int,
    val uuidUser: String,
    val room: String,
    val date: String,
    val timeIn: String,
    val timeOut: String,
    val status: Boolean
)
