package com.ncs.ims_rescuer.ui.intro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.UserInfoDetail
import com.ncs.ims_rescuer.HTTPManager.RepositoryManager.IntroRepository

class IntroViewModel(application: Application) : AndroidViewModel(application){
    private val introRepository = IntroRepository(application)

    fun userInfo(accessToken : String):LiveData<UserInfoDetail>{
        return introRepository.getUserInfo(accessToken)
    }
}