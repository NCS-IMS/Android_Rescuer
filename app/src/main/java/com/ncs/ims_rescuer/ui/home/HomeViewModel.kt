package com.ncs.ims_rescuer.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ncs.imsUser.SaveDataManager.UserInfoData

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val userInfoData = UserInfoData(application)

    val _userImg = MutableLiveData<String>().apply {
        value = userInfoData.getUserData()["IMGURL"]
    }
    private val _userName = MutableLiveData<String>().apply {
        value = userInfoData.getUserData()["NAME"]
    }

    val userName :LiveData<String> = _userName
    val userImg : LiveData<String> = _userImg
}