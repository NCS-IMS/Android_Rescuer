package com.ncs.ims_rescuer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.SaveDataManager.ApplicationSetting
import com.ncs.ims_rescuer.databinding.ActivityMainBinding
import com.ncs.ims_rescuer.databinding.NaviLeftDrawerBinding
import com.ncs.ims_rescuer.ui.home.HomeFragment
import com.ncs.ims_rescuer.ui.notifications.NotificationsFragment
import com.ncs.ims_rescuer.ui.schedule.ScheduleFragment
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import nl.joery.animatedbottombar.AnimatedBottomBar
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import org.w3c.dom.Text
import java.util.*

class MainActivity : AppCompatActivity(), AnimatedBottomBar.OnTabInterceptListener,
    View.OnClickListener {


    lateinit var mainBinding: ActivityMainBinding
    lateinit var fragmentManager: FragmentManager
    lateinit var appSetting: ApplicationSetting
    lateinit var userInfoData: UserInfoData
    lateinit var slidingRootNav: SlidingRootNav


    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.navView.setOnTabInterceptListener(this)

        appSetting = ApplicationSetting(this)
        userInfoData = UserInfoData(this)


        slidingRootNav = SlidingRootNavBuilder(this)
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.navi_left_drawer)
            .inject()

        var a = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var binding : NaviLeftDrawerBinding = DataBindingUtil.inflate(a, R.layout.navi_left_drawer, slidingRootNav.layout.get(0).parent as ViewGroup, false)
        Log.e("dsfsdfsdfxsdfsd", binding.profileName.text.toString())



        mainBinding.menuIcon.setOnClickListener(this)

        initFirebase()
        setNotificationChannel()
        beaconSetup()

    }

    private fun initFirebase() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("Firebase", "getInstanceId failed", task.exception)
                    return@addOnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result!!.token
                FirebaseMessaging.getInstance()
                    .subscribeToTopic(resources.getString(R.string.default_notification_channel_name))
                // Log and toast
                Log.d("Firebase", token)
                appSetting.setFCMToken(token) // FCM 토큰 저장
            }
    }

    private fun setNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence =
                applicationContext.resources.getString(R.string.default_notification_channel_name)
            val description = "Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                applicationContext.resources.getString(R.string.default_notification_channel_id),
                name,
                importance
            )
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onTabIntercepted(
        lastIndex: Int,
        lastTab: AnimatedBottomBar.Tab?,
        newIndex: Int,
        newTab: AnimatedBottomBar.Tab
    ): Boolean {
        var fragment = Fragment()
        when (newTab.id) {
            R.id.navigation_home -> {
                fragment = HomeFragment()
                mainBinding.menuIcon.isVisible = true
            }
            R.id.navigation_schedule -> {
                fragment = ScheduleFragment()
                mainBinding.menuIcon.isVisible = true
            }
            R.id.navigation_notifications -> {
                fragment = NotificationsFragment()
                mainBinding.menuIcon.isVisible = false
            }
        }
        if (fragment != null) {
            fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit()
        } else {
            Log.e("Fragment Create Error", "Error in Creating Fragment")
        }
        return true
    }

    fun beaconSetup() {
        var beacon = Beacon.Builder()
            .setBluetoothName("ncsBeacon")
            .setId1(makeUUID())  // uuid for beacon
            .setId2("1")  // major
            .setId3("1")  // minor
            .setManufacturer(0x0118)  // Radius Networks. 0x0118 : Change this for other beacon layouts // 0x004C : for iPhone
            .setTxPower(-59)  // Power in dB
            .setDataFields(listOf(0L))  // Remove this for beacon layouts without d: fields
            .build();

        var beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
        var beaconTransmitter = BeaconTransmitter(this, beaconParser)
        beaconTransmitter.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                super.onStartSuccess(settingsInEffect)
                Log.e("Beacon start", "Success")
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                Log.e("Beacon start", "Faild")
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Log.e("permission", "GRANTED")
    }

    fun makeUUID(): String { //네이버 ID UUID로 변환 하는 기능
        val charSet = Charsets.UTF_8
        var byt_arr = userInfoData.getUserData()["USER_ID"]!!.toByteArray(charSet)
        val data_uuid = UUID.nameUUIDFromBytes(byt_arr)
        Log.e("UUID", data_uuid.toString())
        Log.e("userid", userInfoData.getUserData()["USER_ID"]!!)
        return data_uuid.toString()
    }

    fun goNotice() {
        if (intent.hasExtra("push")) {
            var bundle = Bundle()
            bundle.putBoolean("push", intent.extras!!.getBoolean("push"))
            NotificationsFragment().arguments = bundle
            fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, NotificationsFragment()).commit()
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            mainBinding.menuIcon.id -> {
                slidingRootNav.openMenu()
                val b = slidingRootNav.layout.get(0).findViewById<TextView>(R.id.profileName)
                Log.e("get Name", b.text.toString())
            }
        }
    }
}