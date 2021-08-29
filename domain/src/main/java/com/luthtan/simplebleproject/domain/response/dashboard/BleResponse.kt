package com.luthtan.simplebleproject.domain.response.dashboard

import com.google.gson.annotations.SerializedName

data class BleResponse(

	@field:SerializedName("name_user")
	val nameUser: String? = null,

	@field:SerializedName("date_user")
	val dateUser: String? = null,

	@field:SerializedName("time_in")
	val timeIn: String? = null,

	@field:SerializedName("status_user")
	val statusUser: Boolean? = null,

	@field:SerializedName("uuid_user")
	val uuidUser: String? = null,

	@field:SerializedName("position_user")
	val positionUser: String? = null,

	@field:SerializedName("number_person_in_room")
	val numberPersonInRoom: Int? = null,

	@field:SerializedName("room_user")
	val roomUser: String? = null,

	@field:SerializedName("time_out")
	val timeOut: String? = null
)
