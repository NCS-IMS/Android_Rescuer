package com.ncs.ims_rescuer.HTTPManager.RepositoryManager

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.NotificationDTO
import com.ncs.ims_rescuer.HTTPManager.DTOManager.NotificationData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.ScheduleData
import com.ncs.ims_rescuer.HTTPManager.RetrofitAPI
import com.ncs.ims_rescuer.HTTPManager.RetrofitInterface
import com.ncs.ims_rescuer.HTTPManager.Tools
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class NotificationRepository(application: Application) {

    private val retrofit : Retrofit = RetrofitAPI.getInstance(Tools().EMERGENCY_URL)
    private val service = retrofit.create(RetrofitInterface::class.java)
    val message =  MutableLiveData<String>()

    fun noticeLog(userId : String, date : String): LiveData<NotificationData> {
        var info = hashMapOf<String, String>(
                "kakaoId" to userId,
                "startDate" to date
        )
        val data = MutableLiveData<NotificationData>()
        service.getUserlog(info).enqueue(object : Callback<NotificationDTO>{
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

}