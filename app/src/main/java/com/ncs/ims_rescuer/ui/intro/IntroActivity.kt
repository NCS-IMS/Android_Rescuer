package com.ncs.ims_rescuer.ui.intro

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ncs.ims_rescuer.LoginActivity
import com.ncs.ims_rescuer.MainActivity
import com.ncs.ims_rescuer.OAthManager.NaverOAthUtil
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.databinding.ActivityIntroBinding
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler

class IntroActivity : AppCompatActivity() {
    lateinit var oAuthLogin: OAuthLogin
    lateinit var introBinding: ActivityIntroBinding
    lateinit var introViewModel: IntroViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        introBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        introViewModel = ViewModelProvider(this).get(IntroViewModel::class.java)

        introBinding.introViewModel = introViewModel
        introBinding.lifecycleOwner = this

        oAuthLogin = OAuthLogin.getInstance()
        var loginUtil = NaverOAthUtil()

        oAuthLogin.init(this, loginUtil.OAUTH_CLIENT_ID, loginUtil.OAUTH_CLIENT_SECRET, loginUtil.OAUTH_CLIENT_NAME)
        oAuthLogin.startOauthLoginActivity(this, oAuthLoginHandler) //네이버 자동로그인 기능 구현

    }
    @SuppressLint("HandlerLeak")
    private var oAuthLoginHandler = object : OAuthLoginHandler(){
        override fun run(state: Boolean) {
            if(state){
                var accessToken = oAuthLogin.getAccessToken(this@IntroActivity)
                var refreshToken = oAuthLogin.getRefreshToken(this@IntroActivity)
                var expireAt = oAuthLogin.getExpiresAt(this@IntroActivity)
                var tokenType = oAuthLogin.getTokenType(this@IntroActivity)
                Log.e("token", accessToken)
                Log.e("expireAt",expireAt.toString())
                getUserInfo(accessToken)
                startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                finish()
            }else{
                var errorCode = oAuthLogin.getLastErrorCode(this@IntroActivity).code
                var errorDesc = oAuthLogin.getLastErrorDesc(this@IntroActivity)
                Toast.makeText(this@IntroActivity, "errorCode: $errorCode errorDesc: $errorDesc", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    fun getUserInfo(accessToken : String){
        var Token = "Bearer $accessToken"
        introViewModel.userInfo(Token).observe(this, {
            Log.e("userName", it.name)
        })
    }
}