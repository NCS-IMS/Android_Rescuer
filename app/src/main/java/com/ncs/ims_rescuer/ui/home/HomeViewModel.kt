package com.ncs.ims_rescuer.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.FireStationData
import com.ncs.ims_rescuer.HTTPManager.RepositoryManager.HomeRepository
import com.ncs.ims_rescuer.HTTPManager.Tools
import com.ncs.ims_rescuer.R

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val userInfoData = UserInfoData(application)
    val homeRepository = HomeRepository(application)

    val _userImg = MutableLiveData<String>().apply {
        value = "${Tools().EMERGENCY_URL}/user/${userInfoData.getUserData()["USER_ID"].toString()}.jpg"
    }
    private val _userName = MutableLiveData<String>().apply {
        value = userInfoData.getUserData()["NAME"]
    }

    private val _setUserInfo = MutableLiveData<String>()

    val userName :LiveData<String> = _userName
    val userImg : LiveData<String> = _userImg


    fun setUserInfo(fcmToken : String, UUID : String): LiveData<String> {
        return homeRepository.setUserData(fcmToken, UUID)
    }
}