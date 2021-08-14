package com.luthtan.simplebleproject.domain.response.dashboard

import com.google.gson.annotations.SerializedName

data class BleResponse (

   /* @field:SerializedName("uuid_user")
    val uuidUser: String,

    @field:SerializedName("room_user")
    val room: String,

    @field:SerializedName("date_user")
    val date: String,

    @field:SerializedName("uuid_time_in")
    val timeIn: String,

    @field:SerializedName("uuid_time_out")
    val timeOut: String,

    @field:SerializedName("status_user")
    val status: Boolean*/

    @field:SerializedName("page")
    var page: Int,

    @field:SerializedName("total_pages")
    var totalPages: Int,

    @field:SerializedName("results")
    var results: Any,

    @field:SerializedName("total_results")
    var totalResults: Int

)