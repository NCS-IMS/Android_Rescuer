package com.ncs.ims_rescuer.HTTPManager.RepositoryManager

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.ScheduleDTO
import com.ncs.ims_rescuer.HTTPManager.DTOManager.ScheduleData
import com.ncs.ims_rescuer.HTTPManager.RetrofitAPI
import com.ncs.ims_rescuer.HTTPManager.RetrofitInterface
import com.ncs.ims_rescuer.HTTPManager.Tools
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ScheduleRepository(application: Application) {
    var mApplication = application
    private val retrofit : Retrofit = RetrofitAPI.getInstance(Tools().EMERGENCY_URL)
    private val service = retrofit.create(RetrofitInterface::class.java)

    fun scheduleList(): LiveData<List<ScheduleData>>{
        var info = hashMapOf<String, String>(
            "id" to "sdfsdfds"
        )
        val data = MutableLiveData<List<ScheduleData>>()
        service.getSchedule(info).enqueue(object : Callback<ScheduleDTO> {
            override fun onResponse(call: Call<ScheduleDTO>, response: Response<ScheduleDTO>) {
                if (response.isSuccessful) {
                    data.value = response.body()!!.scheduleData
                } else {

                }
            }
            override fun onFailure(call: Call<ScheduleDTO>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }
        })
        return data
    }
}