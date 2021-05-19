package com.ncs.ims_rescuer.ItemAdapterManager

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ncs.ims_rescuer.HTTPManager.DTOManager.ScheduleData
import com.ncs.ims_rescuer.databinding.ScheduleItemBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleListAdapter(var context: Context, var scheduleList : List<ScheduleData>): RecyclerView.Adapter<ScheduleListAdapter.ScheduleListViewHolder>(){
    var formatDate = SimpleDateFormat("yyyy년 MM월 dd일 (E) hh시 mm분")
    var getFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") //DateTime 파싱부분
    var date = Date()
    inner class ScheduleListViewHolder(scheduleItemBinding: ScheduleItemBinding) : RecyclerView.ViewHolder(scheduleItemBinding.root) {

        var scheduleTitle = scheduleItemBinding.scheduleTitle
        var carNumTxt = scheduleItemBinding.carNumTxt
        var startTxt = scheduleItemBinding.startTxt
        var endTxt = scheduleItemBinding.endTxt

        fun bind(scheduleData: ScheduleData, context: Context){
            scheduleTitle.text = scheduleData.notice
            carNumTxt.text = scheduleData.car_num
            date = getFormat.parse(scheduleData.startDate)
            startTxt.text = formatDate.format(date)
            date = getFormat.parse(scheduleData.endDate)
            endTxt.text = formatDate.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleListViewHolder {
        var scheduleItemBinding = ScheduleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleListViewHolder(scheduleItemBinding)
    }

    override fun onBindViewHolder(holder: ScheduleListViewHolder, position: Int) {
        holder.bind(scheduleList[position], context)
    }

    override fun getItemCount(): Int = scheduleList.size


}