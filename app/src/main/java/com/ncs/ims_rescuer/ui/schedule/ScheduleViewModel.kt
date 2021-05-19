package com.ncs.ims_rescuer.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.ScheduleData
import com.ncs.ims_rescuer.HTTPManager.RepositoryManager.ScheduleRepository

class ScheduleViewModel(application: Application) : AndroidViewModel(application){
    var mApplication = application
    private val scheduleRepository = ScheduleRepository(application)
    var scheduleMessage = scheduleRepository.message

    fun scheduleList(userId : String): LiveData<List<ScheduleData>>{
        return scheduleRepository.scheduleList(userId)
    }

    fun scheduleMessage(): LiveData<String>{
        return scheduleMessage
    }

}