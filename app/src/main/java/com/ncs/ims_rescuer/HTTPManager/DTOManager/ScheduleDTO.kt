package com.ncs.ims_rescuer.HTTPManager.DTOManager

data class ScheduleDTO(

    var message : String,
    var result : List<ScheduleData>
)

data class ScheduleData(
    var id: Int,
    var car_num: String,
    var notice: String,
    var startDate : String,
    var endDate : String,
)