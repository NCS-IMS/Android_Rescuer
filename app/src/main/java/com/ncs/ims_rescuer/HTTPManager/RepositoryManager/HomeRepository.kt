package com.ncs.ims_rescuer.HTTPManager.RepositoryManager

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.FireStationDTO
import com.ncs.ims_rescuer.HTTPManager.DTOManager.FireStationData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.PublicDTO
import com.ncs.ims_rescuer.HTTPManager.RetrofitAPI
import com.ncs.ims_rescuer.HTTPManager.RetrofitInterface
import com.ncs.ims_rescuer.HTTPManager.Tools
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class HomeRepository(var application: Application) {

    private val retrofit : Retrofit = RetrofitAPI.getInstance(Tools().EMERGENCY_URL)
    private val service = retrofit.create(RetrofitInterface::class.java)
    val message =  MutableLiveData<String>()

    fun getFireStation(x : Double, y : Double):LiveData<List<FireStationData>>{
        val data = MutableLiveData<List<FireStationData>>()
        val info = hashMapOf(
            "x" to x.toString(),
            "y" to y.toString()
        )
        service.getFireStation(info).enqueue(object : Callback<FireStationDTO> {
            override fun onResponse(call: Call<FireStationDTO>, response: Response<FireStationDTO>) {
                if(response.code()==200){
                    data.value = response.body()?.result
                }
            }
            override fun onFailure(call: Call<FireStationDTO>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }
        })
        return data
    }

    fun setUserData(fcmToken : String, UUID : String):LiveData<String>{
        var userInfoData = UserInfoData(application)
        val data = MutableLiveData<String>()
        var info = hashMapOf(
            "kakaoId" to userInfoData.getUserData()["USER_ID"],
            "gender" to if(userInfoData.getUserData()["GENDER"] == "M") "1" else "0",
            "email" to userInfoData.getUserData()["EMAIL"],
            "mobile" to userInfoData.getUserData()["PHONE"],
            "name" to userInfoData.getUserData()["NAME"],
            "birth" to "${userInfoData.getUserData()["BIRTHYEAR"]}-${userInfoData.getUserData()["BIRTHDAY"]}",
            "uuid" to UUID,
            "token" to fcmToken,
            "firestationId" to "0"
        )
        Log.e("hash", userInfoData.getUserData()["GENDER"].toString())
        service.setUserInfo(info).enqueue(object : Callback<PublicDTO> {
            override fun onResponse(call: Call<PublicDTO>, response: Response<PublicDTO>) {
                if(response.code() == 200){
                    data.value = response.body()?.message
                }
            }
            override fun onFailure(call: Call<PublicDTO>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }
        })
        return data
    }
}