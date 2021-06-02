package com.ncs.ims_rescuer.ui.intro

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.kakao.sdk.common.util.Utility
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.UserInfoDetail
import com.ncs.ims_rescuer.ui.login.LoginActivity
import com.ncs.ims_rescuer.MainActivity
import com.ncs.ims_rescuer.OAthManager.NaverOAthUtil
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.SaveDataManager.ApplicationSetting
import com.ncs.ims_rescuer.databinding.ActivityIntroBinding
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler

class IntroActivity : AppCompatActivity() {
    lateinit var oAuthLogin: OAuthLogin
    lateinit var introBinding: ActivityIntroBinding
    lateinit var introViewModel: IntroViewModel
    lateinit var userInfoData: UserInfoData
    lateinit var appSetting: ApplicationSetting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        introBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        introViewModel = ViewModelProvider(this).get(IntroViewModel::class.java)

        introBinding.introViewModel = introViewModel
        introBinding.lifecycleOwner = this

        userInfoData = UserInfoData(this)
        appSetting = ApplicationSetting(this)
        var keyHash = Utility.getKeyHash(this)
        Log.e("hash", keyHash)
        getPermission()


    }

    fun init() {
        oAuthLogin = OAuthLogin.getInstance()
        var loginUtil = NaverOAthUtil()

        oAuthLogin.init(
            this,
            loginUtil.OAUTH_CLIENT_ID,
            loginUtil.OAUTH_CLIENT_SECRET,
            loginUtil.OAUTH_CLIENT_NAME
        )
        oAuthLogin.startOauthLoginActivity(this, oAuthLoginHandler) //네이버 자동로그인 기능 구현

    }

    @SuppressLint("HandlerLeak")
    private var oAuthLoginHandler = object : OAuthLoginHandler() {
        override fun run(state: Boolean) {
            if (state) {
                var accessToken = oAuthLogin.getAccessToken(this@IntroActivity)
                var refreshToken = oAuthLogin.getRefreshToken(this@IntroActivity)
                var expireAt = oAuthLogin.getExpiresAt(this@IntroActivity)
                var tokenType = oAuthLogin.getTokenType(this@IntroActivity)
                getUserInfo(accessToken)
            } else {
                var errorCode = oAuthLogin.getLastErrorCode(this@IntroActivity).code
                var errorDesc = oAuthLogin.getLastErrorDesc(this@IntroActivity)
                Toast.makeText(
                    this@IntroActivity,
                    "errorCode: $errorCode errorDesc: $errorDesc",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    fun getUserInfo(accessToken: String) {
        var Token = "Bearer $accessToken"
        introViewModel.userInfo(Token).observe(this, {
            if (appSetting.getSetting()["first"].toBoolean()) {
//                appSetting.setFirstCheck(false)
                saveInfo(it)
                Log.e("aaffa", it.toString())
            }
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            finish()
        })
    }

    //로그인 사용자 정보 저장
    fun saveInfo(data: UserInfoDetail) {
        userInfoData.setUserID(data.id)
        userInfoData.setName(data.name)
        userInfoData.setPhone(data.mobile)
        userInfoData.setProfileImg(data.profile_image)
        userInfoData.setGender(data.gender)
        userInfoData.setEmail(data.email)
        userInfoData.setBirthday(data.birthday)
        userInfoData.setBirthYear(data.birthyear)
    }


    fun getPermission() {
        var permission = object : PermissionListener {
            override fun onPermissionGranted() {
                init()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                finishAffinity()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleTitle("권한 요청")
            .setRationaleMessage("앱을 사용하기위해서는 권한 허용이 필요합니다!")
            .setDeniedMessage("승인 거부 [설정] > [권한]에서 권한 승인 가능")
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()
    }
}