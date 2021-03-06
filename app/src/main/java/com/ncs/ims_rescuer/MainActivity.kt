package com.ncs.ims_rescuer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.navi.NaviClient
import com.kakao.sdk.navi.model.CoordType
import com.kakao.sdk.navi.model.Location
import com.kakao.sdk.navi.model.NaviOption
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.HTTPManager.RepositoryManager.MainRepository
import com.ncs.ims_rescuer.SaveDataManager.ApplicationSetting
import com.ncs.ims_rescuer.databinding.ActivityMainBinding
import com.ncs.ims_rescuer.databinding.NaviLeftDrawerBinding
import com.ncs.ims_rescuer.ui.changePositionDialog.ChangePositionDialog
import com.ncs.ims_rescuer.ui.home.HomeFragment
import com.ncs.ims_rescuer.ui.login.LoginActivity
import com.ncs.ims_rescuer.ui.notifications.NotificationsFragment
import com.ncs.ims_rescuer.ui.schedule.ScheduleFragment
import com.nhn.android.naverlogin.OAuthLogin
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(), AnimatedBottomBar.OnTabInterceptListener,
    View.OnClickListener {


    lateinit var mainBinding: ActivityMainBinding
    lateinit var fragmentManager: FragmentManager
    lateinit var userInfoData: UserInfoData
    lateinit var slidingRootNav: SlidingRootNav
    lateinit var oAuthLogin : OAuthLogin

    lateinit var navBinding: NaviLeftDrawerBinding
    private val appSetting by lazy {
        ApplicationSetting(this)
    }
    private val logoutText by lazy {
        findViewById<TextView>(R.id.logout)
    }
    private val changeBtn by lazy {
        findViewById<ImageView>(R.id.changeBtn)
    }
    private val userPosition by lazy {
        findViewById<TextView>(R.id.userPosition)
    }

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.navView.setOnTabInterceptListener(this)

        userInfoData = UserInfoData(this)
        oAuthLogin = OAuthLogin.getInstance()

        slidingRootNav = SlidingRootNavBuilder(this)
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.navi_left_drawer)
            .inject()

        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        navBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.navi_left_drawer,
            slidingRootNav.layout[1].parent as ViewGroup,
            false
        )
        mainBinding.menuIcon.setOnClickListener(this)

        initFirebase()
        setNotificationChannel()
        CoroutineScope(Dispatchers.Default).launch {
            beaconSetup()
        }
        initNavSetting()

        registerForContextMenu(changeBtn)
        logoutText.setOnClickListener(this)
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("Firebase", "getInstanceId failed", task.exception)
                    return@addOnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result!!
                FirebaseMessaging.getInstance()
                    .subscribeToTopic(resources.getString(R.string.default_notification_channel_name))
                // Log and toast
                Log.d("Firebase", token)
                appSetting.setFCMToken(token) // FCM ?????? ??????
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

    override fun onCreateContextMenu(menu: ContextMenu?,v: View?,menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val menuInflater = menuInflater
        when(v?.id){
            R.id.changeBtn->{
                menu?.setHeaderTitle("?????? ??????")
                menuInflater.inflate(R.menu.change_menu, menu)
            }
        }

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        super.onContextItemSelected(item)
        Log.e("ds", "sdfsdfsdfd")
        when(item.itemId){
            R.id.photo_menu->{
                var intent = Intent(Intent.ACTION_PICK)
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
                startActivityForResult(intent, 1)
                return true
            }
            R.id.position_menu->{
                val fireStationDialog = ChangePositionDialog(this, userPosition)
                fireStationDialog.show(supportFragmentManager, fireStationDialog.tag)
                return true
            }
        }
        return false
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
            .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")

        //ALTBEACON      m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25
        //EDDYSTONE TLM  x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15
        //EDDYSTONE UID  s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19
        //EDDYSTONE URL  s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v
        //IBEACON        m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24

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

    fun initNavSetting() {
        findViewById<TextView>(R.id.profileName).text = userInfoData.getUserData()["NAME"].toString()
        findViewById<TextView>(R.id.phoneEdit).text = userInfoData.getUserData()["PHONE"].toString()
        findViewById<TextView>(R.id.emailEdit).text = userInfoData.getUserData()["EMAIL"].toString()
        findViewById<TextView>(R.id.genderEdit).text = if(userInfoData.getUserData()["GENDER"]=="M") "??????" else "??????"
        userPosition.text = userInfoData.getUserData()["FIRENAME"].toString()
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

    fun makeUUID(): String { //????????? ID UUID??? ?????? ?????? ??????
        val charSet = Charsets.UTF_8
        var byt_arr = userInfoData.getUserData()["USER_ID"]!!.toByteArray(charSet)
        val data_uuid = UUID.nameUUIDFromBytes(byt_arr)
        CoroutineScope(Dispatchers.Default).launch {
            appSetting.setUUID(data_uuid.toString())
        }
        Log.e("UUID", data_uuid.toString())
        Log.e("userid", userInfoData.getUserData()["USER_ID"]!!)
        return data_uuid.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            mainBinding.menuIcon.id -> {
                slidingRootNav.openMenu()
                val b = slidingRootNav.layout.get(0).findViewById<TextView>(R.id.profileName)
                Log.e("get Name", b.text.toString())
            }
            R.id.logout -> {
                oAuthLogin.logout(this)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && data != null){
            try {
                contentResolver.openInputStream(data?.data!!).use {
                    BitmapFactory.decodeStream(it).let {
                        MainRepository().updateImage(it, userInfoData.getUserData()["USER_ID"].toString()).observe(this,{
                            Log.e("fds", it)
                            Glide.with(this).load(it)
                                .error(Glide.with(this).load(R.drawable.no_image))
                                .apply(RequestOptions()
                                    .signature(ObjectKey(System.currentTimeMillis()))
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                )
                                .into(findViewById(R.id.userImg))
                        })
                    }
                }
            }catch (e : IOException){
                Toast.makeText(this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        startNavi(true)
    }
    private fun startNavi(isStart : Boolean = false){
        Toast.makeText(this, "dsfds", Toast.LENGTH_LONG).show()
        if(isStart){
            /*if (NaviClient.instance.isKakaoNaviInstalled(requireContext())) {
                Log.i("Navi Able", "??????????????? ????????? ????????? ??????")
                startActivity(
                    NaviClient.instance.navigateIntent(
                        Location(noticeData.emAddr, noticeData.longitude, noticeData.latitude),
                        NaviOption(coordType = CoordType.WGS84)
                    )
                )
            } else { //????????? ????????? ?????? ?????? ?????? ????????? ?????? ???????????? ??????
                Log.i("Navi Disable", "??????????????? ?????????: ??? ????????? ?????? ??????")
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("market://details?id=com.locnall.KimGiSa")
                startActivity(intent)
            }*/
        }
    }
}