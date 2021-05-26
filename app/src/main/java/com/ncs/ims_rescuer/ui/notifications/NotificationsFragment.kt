package com.ncs.ims_rescuer.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kakao.sdk.navi.NaviClient
import com.kakao.sdk.navi.model.CoordType
import com.kakao.sdk.navi.model.Location
import com.kakao.sdk.navi.model.NaviOption
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.GISManager.GetMylocation
import com.ncs.ims_rescuer.HTTPManager.DTOManager.NotificationData
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import okhttp3.Dispatcher
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.timer
import kotlin.coroutines.CoroutineContext

class NotificationsFragment : Fragment(), MapView.MapViewEventListener, View.OnClickListener{

    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var notificationsBinding: FragmentNotificationsBinding
    lateinit var map_view : MapView
    lateinit var marker : MapPOIItem
    lateinit var gps : HashMap<String, Double>
    lateinit var userInfoData: UserInfoData
    var format = SimpleDateFormat("yyyy-MM-dd")
    var formatDatetime = SimpleDateFormat("yyyy년 MM월 dd일 (E) hh시 mm분")
    var getFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") //DateTime 파싱부분
    var expened = false
    lateinit var noticeData : NotificationData
    var loopActive:Boolean = true
    val job by lazy { Job() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        notificationsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)

        notificationsBinding.notificationsViewModel = notificationsViewModel
        notificationsBinding.lifecycleOwner = this

        userInfoData = UserInfoData(requireContext())

        gps = GetMylocation().getLocation(requireContext())
        map_view = MapView(requireActivity())
        notificationsBinding.mapView.addView(map_view)
        setCurrentLocation() //현재 위치표시
        getNotice() //최근 요청 목록 불러오기

        getMyLocation()

        notificationsBinding.userCard.setOnClickListener(this)
        notificationsBinding.naviBtn.setOnClickListener(this)
        return notificationsBinding.root
    }
    //위치 마커 기능
    fun setCurrentLocation(){
        var mapPoint = MapPoint.mapPointWithGeoCoord(gps.get("latitude")!!, gps.get("longitude")!!)
        map_view.setMapCenterPointAndZoomLevel(mapPoint, 5, true)
        map_view.zoomIn(true)
        map_view.zoomOut(true)
        map_view.setMapViewEventListener(this) //지도 변환 이벤트 설정
        map_view.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading //지도 마커 옵션 설정 (현재 현위치 트래킹 모드 + 나침반 모드 활성화)
    }

    fun getNotice(){
        var today = format.format(Calendar.getInstance().time)
        Log.e("sdf", today)
        notificationsViewModel.getNotice(userInfoData.getUserData()["USER_ID"].toString(), today).observe(viewLifecycleOwner, {
            var date = getFormat.parse(it.createDate)
            notificationsBinding.stateTitle.text = "${it.state}환자가 발생하였습니다."
            notificationsBinding.currentAddr.text = "${it.emAddr}"
            notificationsBinding.callTime.text = "${formatDatetime.format(date)}"
            notificationsBinding.userAddr.text = "${it.userAddr}"
            notificationsBinding.medicine.text = "${it.medicine}"
            notificationsBinding.history.text = "${it.anamnesis}"
            var mapPoint = MapPoint.mapPointWithGeoCoord(it.latitude.toDouble(),it.longitude.toDouble())
            setCurretMarker(mapPoint, "환자 위치")
            noticeData = it
        })
    }

    fun setCurretMarker(mapPoint : MapPoint, pointName : String){
        marker = MapPOIItem()
        marker.itemName = pointName
        marker.mapPoint = mapPoint
        marker.tag = 0
        marker.markerType = MapPOIItem.MarkerType.BluePin
        map_view.addPOIItem(marker)
    }

    fun getMyLocation() = runBlocking{
         CoroutineScope(Dispatchers.Main+job).launch{
            while(loopActive){
                view?.let {
                    notificationsViewModel.getLocation().observe(viewLifecycleOwner, {
                        notificationsBinding.currentLocation.text = it.address_name
                    })
                }
                delay(1000L)
            }
        }
    }
    override fun onClick(v: View?) {
        when(v?.id){
            notificationsBinding.userCard.id ->{
                if(!expened){
                    notificationsBinding.expandedLayout.visibility = View.VISIBLE
                    expened = !expened
                }else{
                    notificationsBinding.expandedLayout.visibility = View.GONE
                    expened = !expened
                }
            }
            notificationsBinding.naviBtn.id -> {
                //카카오 네비가 설치 되어 있으면 바로 연결
                if (NaviClient.instance.isKakaoNaviInstalled(requireContext())) {
                    Log.i("Navi Able", "카카오내비 앱으로 길안내 가능")
                    startActivity(
                            NaviClient.instance.navigateIntent(
                                    Location(noticeData.emAddr, noticeData.longitude, noticeData.latitude),
                                    NaviOption(coordType = CoordType.WGS84)
                            )
                    )
                } else { //카카오 네비가 설치 되어 있지 않으면 설치 페이지로 이동
                    Log.i("Navi Disable", "카카오내비 미설치: 웹 길안내 사용 권장")
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.data = Uri.parse("market://details?id=com.locnall.KimGiSa")
                    startActivity(intent)
                }
            }
        }
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

   
}