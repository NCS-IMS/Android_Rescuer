package com.ncs.ims_rescuer.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {

    private lateinit var scheduleViewModel: ScheduleViewModel
    lateinit var scheduleBinding: FragmentScheduleBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        scheduleViewModel = ViewModelProvider(requireActivity()).get(ScheduleViewModel::class.java)
        scheduleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false)

        scheduleBinding.scheduleViewModel = scheduleViewModel
        scheduleBinding.lifecycleOwner = this
        scheduleViewModel.scheduleList

        return scheduleBinding.root
    }

}