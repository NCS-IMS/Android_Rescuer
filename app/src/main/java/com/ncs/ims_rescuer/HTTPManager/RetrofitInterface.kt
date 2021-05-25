package com.ncs.ims_rescuer.HTTPManager

import com.ncs.ims_rescuer.HTTPManager.DTOManager.*
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface{
    @POST("/schedule/find/one")
    fun getSchedule(@Body data: HashMap<String, String>): Call<ScheduleDTO>

    @GET("/v1/nid/me")
    fun getUserInfo(@Header("Authorization") authHeader : String): Call<UserInfoDTO>

    @POST("/user/create")
    fun setUserInfo(@Body data: HashMap<String, String>):Call<PublicDTO>

    @POST("/user/log")
    fun getUserlog(@Body data: HashMap<String, String>):Call<NotificationDTO>

    @POST("/v2/local/geo/coord2address.json")
    fun getLocation(@Header("Authorization") authHeader: String, @QueryMap data: HashMap<String, String>):Call<CurrentLocationDTO>
}