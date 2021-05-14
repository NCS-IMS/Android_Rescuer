package com.ncs.ims_rescuer.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.UserInfoDetail
import com.ncs.ims_rescuer.HTTPManager.RepositoryManager.UserRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val loginRepository = UserRepository(application)

    fun userInfo(accessToken : String): LiveData<UserInfoDetail> {
        return loginRepository.getUserInfo(accessToken)
    }
}