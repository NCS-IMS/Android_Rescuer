package com.ncs.ims_rescuer.HTTPManager

import com.ncs.ims_rescuer.HTTPManager.DTOManager.ScheduleDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET

interface RetrofitInterface{
    @GET("/user/create")
    fun getSchedule(@Body data: HashMap<String, String>): Call<ScheduleDTO>
}