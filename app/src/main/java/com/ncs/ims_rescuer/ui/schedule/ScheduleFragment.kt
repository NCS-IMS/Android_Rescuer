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

        getScheduleViewModel()

        return scheduleBinding.root
    }

    fun getScheduleViewModel(){
        scheduleViewModel.scheduleList().observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()){
                for(i in it){
                    Log.e("dsf", i.car_num)
                }
            }
        })
        scheduleViewModel.scheduleMessage().observe(viewLifecycleOwner, Observer {
            Log.e("sdfd", it.toString())
        })
    }

}