package com.ncs.ims_rescuer.ui.changePositionDialog

import android.content.Context
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.GISManager.GetMylocation
import com.ncs.ims_rescuer.HTTPManager.DTOManager.FireStationData
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.databinding.ActivityMainBinding
import com.ncs.ims_rescuer.databinding.FindFirestationBinding
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class ChangePositionDialog(context: Context, var userPosition: TextView): DialogFragment(), MapView.MapViewEventListener, View.OnClickListener {

    private lateinit var find_fireStation :FindFirestationBinding
    private lateinit var changePositionViewModel: ChangePositionViewModel
    private val map_view by lazy {
        MapView(requireActivity())
    }
    private val gps by lazy {
        GetMylocation().getLocation(context)
    }

    private var hiddenExpened: Boolean = false
    private var cardVisible : Boolean = false

    lateinit var fireStationMarker :MapPOIItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
    }

    var loopActive:Boolean = true
    val job by lazy { Job() }

    private val markerEventListener by lazy {
        MarkerEventListener(this)
    }

    private val stationData : HashMap<String, FireStationData> by lazy {
        HashMap()
    }

    private val userInfoData by lazy {
        UserInfoData(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        find_fireStation = DataBindingUtil.inflate(inflater, R.layout.find_firestation, container, false)
        changePositionViewModel = ViewModelProvider(this).get(ChangePositionViewModel::class.java)
        find_fireStation.changePositionViewModel = changePositionViewModel
        find_fireStation.lifecycleOwner = this

        find_fireStation.mapView.addView(map_view)
        setCurrentLocation()
        getMyLocation()

        getFireStation()

        find_fireStation.stationCard.setOnClickListener(this)
        find_fireStation.saveUserPosition.setOnClickListener(this)
        map_view.setPOIItemEventListener(markerEventListener)
        return find_fireStation.root
    }


    fun setCurrentLocation(){
        var mapPoint = MapPoint.mapPointWithGeoCoord(gps.get("latitude")!!, gps.get("longitude")!!)
        map_view.setMapCenterPointAndZoomLevel(mapPoint, 7, true)
        map_view.zoomIn(true)
        map_view.zoomOut(true)
        map_view.setMapViewEventListener(this) //지도 변환 이벤트 설정
        map_view.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading //지도 마커 옵션 설정 (현재 현위치 트래킹 모드 + 나침반 모드 활성화)
    }

    fun setLocationMaker(fireStationData: FireStationData){
        stationData[fireStationData.place_name] = fireStationData //마커 정보 저장
        fireStationMarker = MapPOIItem()
        fireStationMarker.apply {
            var mapPoint = MapPoint.mapPointWithGeoCoord(fireStationData.y.toDouble(), fireStationData.x.toDouble())
            fireStationMarker.itemName = fireStationData.place_name
            fireStationMarker.mapPoint = mapPoint
            fireStationMarker.tag = 0
            fireStationMarker.markerType = MapPOIItem.MarkerType.CustomImage
            isCustomImageAutoscale = true
            customImageResourceId = R.drawable.marker_firestation
        }
        map_view.addPOIItem(fireStationMarker)
    }

    fun getMyLocation() = runBlocking{
        CoroutineScope(Dispatchers.Main+job).launch{
            while(loopActive){
                view?.let {
                    changePositionViewModel.getLocation().observe(viewLifecycleOwner, {
                        find_fireStation.currentLocation.text = it.address_name
                    })
                }
                delay(1000L)
            }
        }
    }

    fun getFireStation(){
        changePositionViewModel.getFireStation().observe(viewLifecycleOwner, {
            it.forEach{ element ->
                setLocationMaker(element)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        loopActive = false
    }
    override fun onMapViewInitialized(p0: MapView?) {}
    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {}
    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}

    override fun onClick(v: View?) {
        when(v?.id){
            find_fireStation.stationCard.id ->{
                if(!hiddenExpened){
                    find_fireStation.expandedLayout.visibility = View.VISIBLE
                    hiddenExpened = !hiddenExpened
                }else{
                    find_fireStation.expandedLayout.visibility = View.GONE
                    hiddenExpened = !hiddenExpened
                }
            }
            find_fireStation.saveUserPosition.id->{
                changePositionViewModel.setFireStatinID(userInfoData.getUserData()["USER_ID"].toString(), find_fireStation.stationtId.text.toString())
                    .observe(viewLifecycleOwner,{
                        userInfoData.setFireStationId(find_fireStation.stationtId.text.toString())
                        userInfoData.setFireStationName(find_fireStation.stationName.text.toString())
                        userPosition.text = find_fireStation.stationName.text.toString()
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        dismiss()
                    })
            }
        }
    }
    inner class MarkerEventListener(val context: ChangePositionDialog): MapView.POIItemEventListener{
        override fun onPOIItemSelected(mapView: MapView?, item: MapPOIItem?) {
            var fireStationData = stationData[item?.itemName]

            if(!cardVisible){
                find_fireStation.stationCard.visibility = View.VISIBLE
            }
            find_fireStation.stationName.text = fireStationData?.place_name
            find_fireStation.roadAddr.text = fireStationData?.road_address_name
            find_fireStation.phoneNum.text = fireStationData?.phone
            find_fireStation.phoneNum.text = fireStationData?.phone
            find_fireStation.stationtId.text = fireStationData?.id
            find_fireStation.addrText.text = fireStationData?.address_name
        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?,p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {}

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}

    }
}