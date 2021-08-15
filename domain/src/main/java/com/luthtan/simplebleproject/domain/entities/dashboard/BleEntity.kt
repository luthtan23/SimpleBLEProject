package com.luthtan.simplebleproject.domain.entities.dashboard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_ble_table")
data class BleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @field:SerializedName("uuid_user")
    @ColumnInfo(defaultValue = "", name = "uuid_user")
    val uuidUser: String,

    @field:SerializedName("room_user")
    @ColumnInfo(defaultValue = "", name = "room_user")
    val room: String,

    @field:SerializedName("date_user")
    @ColumnInfo(defaultValue = "", name = "date_user")
    val date: String,

    @field:SerializedName("time_in")
    @ColumnInfo(defaultValue = "", name = "time_in")
    val timeIn: String,

    @field:SerializedName("time_out")
    @ColumnInfo(defaultValue = "", name = "time_out")
    val timeOut: String,

    @field:SerializedName("status_user")
    @ColumnInfo(defaultValue = "", name = "status_user")
    val status: Boolean,

    @field:SerializedName("name_user")
    @ColumnInfo(defaultValue = "", name = "name_user")
    val name: String,

    @field:SerializedName("position_user")
    @ColumnInfo(defaultValue = "", name = "position_user")
    val position: String,

    @field:SerializedName("number_person_in_room")
    @ColumnInfo(defaultValue = "", name = "number_person_in_room")
    val numberPersonInRoom: Int
)
