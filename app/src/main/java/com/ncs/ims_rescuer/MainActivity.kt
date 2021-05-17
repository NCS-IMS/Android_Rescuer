package com.ncs.ims_rescuer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.SaveDataManager.ApplicationSetting
import com.ncs.ims_rescuer.databinding.ActivityMainBinding
import com.ncs.ims_rescuer.ui.home.HomeFragment
import com.ncs.ims_rescuer.ui.notifications.NotificationsFragment
import com.ncs.ims_rescuer.ui.schedule.ScheduleFragment
import nl.joery.animatedbottombar.AnimatedBottomBar

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

        initFirebase()
        setNotificationChannel()
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

    fun setUserData(fcmToken: String, mac : String){

    }

    override fun onTabIntercepted(lastIndex: Int,lastTab: AnimatedBottomBar.Tab?,newIndex: Int,newTab: AnimatedBottomBar.Tab): Boolean {
        var fragment = Fragment()
        when(newTab.id){
            R.id.navigation_home->{
                fragment = HomeFragment()
            }
            R.id.navigation_schedule->{
                fragment = ScheduleFragment()
            }
            R.id.navigation_notifications->{
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


}