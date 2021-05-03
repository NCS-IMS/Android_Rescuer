package com.ncs.ims_rescuer.HTTPManager.DTOManager

data class ScheduleDTO(

    var message : String,
    var scheduleData : List<ScheduleData>
)

data class ScheduleData(
    var car_num: String,
    var startDate : String,
    var endDate : String,
    var fireStationId : String,
    var mans : String
)