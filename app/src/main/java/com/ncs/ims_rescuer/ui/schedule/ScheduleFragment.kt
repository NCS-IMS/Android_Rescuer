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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        scheduleViewModel = ViewModelProvider(requireActivity()).get(ScheduleViewModel::class.java)
        scheduleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false)

        scheduleBinding.scheduleViewModel = scheduleViewModel
        scheduleBinding.lifecycleOwner = this

        getScheduleViewModel()

        return scheduleBinding.root
    }

    fun getScheduleViewModel(){
        var scheduleList = ArrayList<ScheduleData>()

        scheduleViewModel.scheduleList().observe(viewLifecycleOwner, Observer {
            if(it.size == 0){
                scheduleBinding.noScheduleLayout.visibility = View.VISIBLE
                scheduleBinding.rescuerSchedule.visibility = View.GONE
            }else{
                scheduleBinding.noScheduleLayout.visibility = View.GONE
                scheduleBinding.rescuerSchedule.visibility = View.VISIBLE
                for(i in it){
                    scheduleList.add(i)
                }
                initVerticalRecyclerView(scheduleList)
            }

        })
        scheduleViewModel.scheduleMessage().observe(viewLifecycleOwner, Observer {
            Log.e("sdfd", it.toString())
        })
    }

    private fun initVerticalRecyclerView(scheduleList : ArrayList<ScheduleData>) {
        scheduleBinding.rescuerSchedule.adapter = ScheduleListAdapter(requireContext(), scheduleList)

        //Currently only LinearLayoutManager is supported.
        scheduleBinding.rescuerSchedule.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL, false)

        scheduleBinding.rescuerSchedule.addItemDecoration(getSectionCallback(scheduleList))
    }
    //Get SectionCallback method
    private fun getSectionCallback(singerList: List<ScheduleData>): SectionCallback {
        return object : SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                singerList[position].startDate != singerList[position - 1].startDate

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? {
                val singer = singerList[position]
                return SectionInfo(singer.startDate)
            }

        }
    }
}