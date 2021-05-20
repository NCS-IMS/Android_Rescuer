package com.ncs.ims_rescuer.HTTPManager.DTOManager

import com.google.gson.annotations.SerializedName

data class ScheduleDTO(
    @SerializedName("message")
    var message : String,
    @SerializedName("result")
    var result : List<ScheduleData>
)

data class ScheduleData(
    @SerializedName("id")
    var id: Int,
    @SerializedName("car_num")
    var car_num: String,
    @SerializedName("notice")
    var notice: String,
    @SerializedName("startDate")
    var startDate : String,
    @SerializedName("endDate")
    var endDate : String,
)