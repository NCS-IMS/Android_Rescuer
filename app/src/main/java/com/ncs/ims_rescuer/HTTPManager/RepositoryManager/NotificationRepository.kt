package com.ncs.ims_rescuer.HTTPManager.RepositoryManager

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ncs.ims_rescuer.GISManager.GetMylocation
import com.ncs.ims_rescuer.HTTPManager.DTOManager.*
import com.ncs.ims_rescuer.HTTPManager.RetrofitAPI
import com.ncs.ims_rescuer.HTTPManager.RetrofitInterface
import com.ncs.ims_rescuer.HTTPManager.Tools
import com.ncs.ims_rescuer.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class NotificationRepository(var application: Application) {

    private val retrofit : Retrofit = RetrofitAPI.getInstance(Tools().EMERGENCY_URL)
    private val KakaoAPI : Retrofit = RetrofitAPI.getInstance(Tools().Kakao_MAP_URL)
    private val service = arrayOf(retrofit.create(RetrofitInterface::class.java), KakaoAPI.create(RetrofitInterface::class.java))
    val message =  MutableLiveData<String>()

    fun noticeLog(userId : String, date : String): LiveData<NotificationData> {
        var info = hashMapOf<String, String>(
                "kakaoId" to userId,
                "startDate" to date
        )
        val data = MutableLiveData<NotificationData>()
        service[0].getUserlog(info).enqueue(object : Callback<NotificationDTO>{
            override fun onResponse(call: Call<NotificationDTO>, response: Response<NotificationDTO>) {
                if(response.code() == 200){
                    data.value = response.body()?.result
                }else{
                    message.value = response.body()?.message
                }
            }

            override fun onFailure(call: Call<NotificationDTO>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }
        })
        return data
    }

    fun getLocation():LiveData<AddressData>{
        var authHeader = "KakaoAK ${application.getString(R.string.kakao_restapi_key)}"
        var gps = GetMylocation().getLocation(application)
        var info = hashMapOf(
            "x" to gps.get("longitude").toString(),
            "y" to gps.get("latitude").toString()
        )
        val data = MutableLiveData<AddressData>()
        service[1].getLocation(authHeader, info).enqueue(object : Callback<CurrentLocationDTO>{
            override fun onResponse(call: Call<CurrentLocationDTO>,response: Response<CurrentLocationDTO>) {
                Log.e("sdfsd", info.toString())
                if(response.code() == 200){
                    data.value = response.body()?.documents?.get(0)?.address
                }
            }
            override fun onFailure(call: Call<CurrentLocationDTO>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }
        })
        return data
    }

}