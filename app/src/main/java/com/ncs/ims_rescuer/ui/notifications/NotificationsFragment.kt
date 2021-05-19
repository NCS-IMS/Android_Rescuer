package com.ncs.ims_rescuer.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ncs.ims_rescuer.GISManager.GetMylocation
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.databinding.FragmentNotificationsBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var notificationsBinding: FragmentNotificationsBinding
    lateinit var map_view : MapView
    lateinit var marker : MapPOIItem
    lateinit var gps : HashMap<String, Double>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        notificationsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)

        notificationsBinding.notificationsViewModel = notificationsViewModel
        notificationsBinding.lifecycleOwner = this

        gps = GetMylocation().getLocation(requireContext())
        map_view = MapView(requireActivity())
        notificationsBinding.mapView.addView(map_view)
        setCurrentLocation()
        return notificationsBinding.root
    }
    //위치 마커 기능
    fun setCurrentLocation(){
        var mapPoint = MapPoint.mapPointWithGeoCoord(gps.get("latitude")!!, gps.get("longitude")!!)
        map_view.setMapCenterPointAndZoomLevel(mapPoint, 5, true)
        map_view.zoomIn(true)
        map_view.zoomOut(true)
        setCurretMarker(mapPoint)
    }

    fun setCurretMarker(mapPoint : MapPoint){
        marker = MapPOIItem()
        marker.itemName = "내위치"
        marker.mapPoint = mapPoint
        marker.tag = 0
        marker.markerType = MapPOIItem.MarkerType.BluePin
        map_view.addPOIItem(marker)
    }
}