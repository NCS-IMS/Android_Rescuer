package com.ncs.ims_rescuer.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.UserInfoDetail
import com.ncs.ims_rescuer.MainActivity
import com.ncs.ims_rescuer.OAthManager.NaverOAthUtil
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.SaveDataManager.ApplicationSetting
import com.ncs.ims_rescuer.databinding.ActivityLoginBinding
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler

class LoginActivity : AppCompatActivity() {
    lateinit var loginBinding: ActivityLoginBinding
    lateinit var loginViewModel: LoginViewModel
    lateinit var oAuthLogin: OAuthLogin
    lateinit var userInfoData:UserInfoData
    lateinit var appSetting : ApplicationSetting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginBinding.loginViewModel = loginViewModel
        loginBinding.lifecycleOwner = this

        userInfoData = UserInfoData(this)
        appSetting = ApplicationSetting(this)

        init()
    }

    private fun init(){
        oAuthLogin = OAuthLogin.getInstance()
        var loginUtil = NaverOAthUtil()
        oAuthLogin.init(this, loginUtil.OAUTH_CLIENT_ID, loginUtil.OAUTH_CLIENT_SECRET, loginUtil.OAUTH_CLIENT_NAME)
        loginBinding.naverLogin.setOAuthLoginHandler(oAuthLoginHandler)
    }

    @SuppressLint("HandlerLeak")
    private var oAuthLoginHandler = object : OAuthLoginHandler(){
        override fun run(state: Boolean) {
            if(state){
                var accessToken = oAuthLogin.getAccessToken(this@LoginActivity)
                var refreshToken = oAuthLogin.getRefreshToken(this@LoginActivity)
                var expireAt = oAuthLogin.getExpiresAt(this@LoginActivity)
                var tokenType = oAuthLogin.getTokenType(this@LoginActivity)
                getUserInfo(accessToken)
            }else{
                var errorCode = oAuthLogin.getLastErrorCode(this@LoginActivity).code
                var errorDesc = oAuthLogin.getLastErrorDesc(this@LoginActivity)
                Toast.makeText(this@LoginActivity, "errorCode: $errorCode errorDesc: $errorDesc", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getUserInfo(accessToken : String){
        var Token = "Bearer $accessToken"
        loginViewModel.userInfo(Token).observe(this, {
            if(!appSetting.getSetting()["first"].toBoolean()) {
                appSetting.setFirstCheck(true)
                saveInfo(it)
            }
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        })
    }

    //로그인 사용자 정보 저장
    fun saveInfo(data : UserInfoDetail){
        userInfoData.setUserID(data.id)
        userInfoData.setName(data.name)
        userInfoData.setPhone(data.mobile)
        userInfoData.setProfileImg(data.profile_image)
        userInfoData.setGender(data.gender)
        userInfoData.setEmail(data.email)
        userInfoData.setBirthday(data.birthday)
        userInfoData.setBirthYear(data.birthyear)
    }
}