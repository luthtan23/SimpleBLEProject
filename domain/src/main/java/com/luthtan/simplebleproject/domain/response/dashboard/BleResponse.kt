package com.luthtan.simplebleproject.domain.response.dashboard

import com.google.gson.annotations.SerializedName

data class BleResponse (

    @field:SerializedName("uuid_user")
    val uuidUser: String,

    @field:SerializedName("room_user")
    val room: String,

    @field:SerializedName("date_user")
    val date: String,

    @field:SerializedName("time_in")
    val timeIn: String,

    @field:SerializedName("time_out")
    val timeOut: String,

    @field:SerializedName("status_user")
    val status: Boolean

)