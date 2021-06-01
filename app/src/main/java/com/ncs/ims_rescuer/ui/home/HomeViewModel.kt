package com.ncs.ims_rescuer.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.FireStationData
import com.ncs.ims_rescuer.HTTPManager.RepositoryManager.HomeRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val userInfoData = UserInfoData(application)
    val homeRepository = HomeRepository(application)

    val _userImg = MutableLiveData<String>().apply {
        value = userInfoData.getUserData()["IMGURL"]
    }
    private val _userName = MutableLiveData<String>().apply {
        value = userInfoData.getUserData()["NAME"]
    }

    private val _setUserInfo = MutableLiveData<String>()

    val userName :LiveData<String> = _userName
    val userImg : LiveData<String> = _userImg

    fun getFireStation(x : Double, y : Double): LiveData<List<FireStationData>> {
        return homeRepository.getFireStation(x, y)
    }

    fun setUserInfo(fcmToken : String, UUID : String): LiveData<String> {
        return homeRepository.setUserData(fcmToken, UUID)
    }
}