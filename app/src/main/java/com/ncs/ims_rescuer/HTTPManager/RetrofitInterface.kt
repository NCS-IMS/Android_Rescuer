package com.ncs.ims_rescuer.HTTPManager

import com.ncs.ims_rescuer.HTTPManager.DTOManager.PublicDTO
import com.ncs.ims_rescuer.HTTPManager.DTOManager.ScheduleDTO
import com.ncs.ims_rescuer.HTTPManager.DTOManager.UserInfoDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RetrofitInterface{
    @POST("/schedule/find/one")
    fun getSchedule(@Body data: HashMap<String, String>): Call<ScheduleDTO>

    @GET("/v1/nid/me")
    fun getUserInfo(@Header("Authorization") authHeader : String): Call<UserInfoDTO>

    @POST("/user/create")
    fun setUserInfo(@Body data: HashMap<String, String>):Call<PublicDTO>
}