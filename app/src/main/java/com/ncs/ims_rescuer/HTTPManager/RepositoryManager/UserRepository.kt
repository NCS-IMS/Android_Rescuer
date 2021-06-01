package com.ncs.ims_rescuer.HTTPManager.RepositoryManager

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.*
import com.ncs.ims_rescuer.HTTPManager.RetrofitAPI
import com.ncs.ims_rescuer.HTTPManager.RetrofitInterface
import com.ncs.ims_rescuer.HTTPManager.Tools
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class UserRepository(var application: Application) {
    private val retrofit : Retrofit = RetrofitAPI.getInstance(Tools().NAVER_LOGIN_URL)
    private val service = retrofit.create(RetrofitInterface::class.java)
    val message =  MutableLiveData<String>()

    fun getUserInfo(authHeader : String):LiveData<UserInfoDetail>{
        val data = MutableLiveData<UserInfoDetail>()
        service.getUserInfo(authHeader).enqueue(object : Callback<UserInfoDTO>{
            override fun onResponse(call: Call<UserInfoDTO>, response: Response<UserInfoDTO>) {
                if(response.code() == 200) {
                    data.value = response.body()?.response
                }else{
                    message.value = response.body()?.message
                }
            }

            override fun onFailure(call: Call<UserInfoDTO>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }
        })
        return data
    }
}