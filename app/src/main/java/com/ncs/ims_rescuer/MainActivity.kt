package com.ncs.ims_rescuer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.SaveDataManager.ApplicationSetting
import com.ncs.ims_rescuer.databinding.ActivityMainBinding
import com.ncs.ims_rescuer.ui.home.HomeFragment
import com.ncs.ims_rescuer.ui.notifications.NotificationsFragment
import com.ncs.ims_rescuer.ui.schedule.ScheduleFragment
import nl.joery.animatedbottombar.AnimatedBottomBar
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.nio.ByteBuffer
import java.util.*

class MainActivity : AppCompatActivity(), AnimatedBottomBar.OnTabInterceptListener{


    lateinit var mainBinding: ActivityMainBinding
    lateinit var fragmentManager:FragmentManager
    lateinit var appSetting : ApplicationSetting
    lateinit var userInfoData: UserInfoData

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.navView.setOnTabInterceptListener(this)

        appSetting = ApplicationSetting(this)
        userInfoData = UserInfoData(this)
        var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        Log.e("blue", bluetoothAdapter.address)
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
                FirebaseMessaging.getInstance().subscribeToTopic(resources.getString(R.string.default_notification_channel_name))
                // Log and toast
                Log.d("Firebase", token)
                appSetting.setFCMToken(token) // FCM 토큰 저장
            }
    }

    private fun setNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = applicationContext.resources.getString(R.string.default_notification_channel_name)
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

    fun setUserData(fcmToken: String, mac: String){

    }

    override fun onTabIntercepted(
        lastIndex: Int,
        lastTab: AnimatedBottomBar.Tab?,
        newIndex: Int,
        newTab: AnimatedBottomBar.Tab
    ): Boolean {
        var fragment = Fragment()
        when(newTab.id){
            R.id.navigation_home -> {
                fragment = HomeFragment()
            }
            R.id.navigation_schedule -> {
                fragment = ScheduleFragment()
            }
            R.id.navigation_notifications -> {
                fragment = NotificationsFragment()
            }
        }
        if(fragment != null){
            fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit()
        }else{
            Log.e("Fragment Create Error", "Error in Creating Fragment")
        }
        return true
    }

    fun beaconSetup(){
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

    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Log.e("permission", "GRANTED")
    }

    fun makeUUID(): String{ //네이버 ID UUID로 변환 하는 기능
        val charSet = Charsets.UTF_8
        var byt_arr = userInfoData.getUserData()["USER_ID"]!!.toByteArray(charSet)
        val data_uuid = UUID.nameUUIDFromBytes(byt_arr)
        Log.e("UUID", data_uuid.toString())
        Log.e("userid", userInfoData.getUserData()["USER_ID"]!!)
        return data_uuid.toString()
    }


}