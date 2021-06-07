package com.ncs.ims_rescuer.ui.changePositionDialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.AddressData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.FireStationData
import com.ncs.ims_rescuer.HTTPManager.RepositoryManager.ChangePositionRepository
import com.ncs.ims_rescuer.HTTPManager.RepositoryManager.NotificationRepository

class ChangePositionViewModel(application: Application):AndroidViewModel(application) {
    private val changePositionRepository = ChangePositionRepository(application)
    fun getLocation(): LiveData<AddressData> {
        return changePositionRepository.getLocation()
    }

    fun getFireStation(): LiveData<List<FireStationData>>{
        return changePositionRepository.getFireStation()
    }
    fun setFireStatinID(kakaoId : String, fireStationId: String):LiveData<String>{
        return changePositionRepository.setFireStatinID(kakaoId, fireStationId)
    }
}