package com.ncs.ims_rescuer.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ncs.ims_rescuer.HTTPManager.RepositoryManager.ScheduleRepository

class ScheduleViewModel(application: Application) : AndroidViewModel(application){
    var mApplication = application
    private val scheduleRepository = ScheduleRepository(application)
    val scheduleList = scheduleRepository.scheduleList()

}