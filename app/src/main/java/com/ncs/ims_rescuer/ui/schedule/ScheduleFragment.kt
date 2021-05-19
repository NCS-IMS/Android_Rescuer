package com.ncs.ims_rescuer.ui.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.ScheduleData
import com.ncs.ims_rescuer.ItemAdapterManager.ScheduleListAdapter
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.databinding.FragmentScheduleBinding
import xyz.sangcomz.stickytimelineview.callback.SectionCallback
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import java.text.SimpleDateFormat

class ScheduleFragment : Fragment() {

    private lateinit var scheduleViewModel: ScheduleViewModel
    lateinit var scheduleBinding: FragmentScheduleBinding
    lateinit var userInfoData: UserInfoData
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        scheduleViewModel = ViewModelProvider(requireActivity()).get(ScheduleViewModel::class.java)
        scheduleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false)

        scheduleBinding.scheduleViewModel = scheduleViewModel
        scheduleBinding.lifecycleOwner = this
        userInfoData = UserInfoData(requireContext())
        scheduleBinding.userDateTimeline.setInitialDate(2021, 5, 19)
        scheduleViewModel.scheduleList(userInfoData.getUserData()["USER_ID"].toString()).observe(viewLifecycleOwner, {
            Log.e("fsddsfsd", it.toString())
            setScheduleList(it)
        })
        return scheduleBinding.root
    }

    fun setScheduleList(scheduleList : List<ScheduleData>){
        val linearManger = LinearLayoutManager(requireContext())
        var scheduleListAdapter = ScheduleListAdapter(requireContext(), scheduleList)
        scheduleBinding.scheduleList.adapter = scheduleListAdapter
        scheduleBinding.scheduleList.layoutManager = linearManger
        scheduleBinding.scheduleList.setHasFixedSize(true)
        scheduleListAdapter.notifyDataSetChanged()
    }

}