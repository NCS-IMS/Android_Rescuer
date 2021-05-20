package com.ncs.ims_rescuer.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ncs.ims_rescuer.HTTPManager.DTOManager.NotificationData
import com.ncs.ims_rescuer.HTTPManager.RepositoryManager.NotificationRepository

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationRepository = NotificationRepository(application)

    fun getNotice(userId : String, date : String):LiveData<NotificationData>{
        return notificationRepository.noticeLog(userId, date)
    }

}