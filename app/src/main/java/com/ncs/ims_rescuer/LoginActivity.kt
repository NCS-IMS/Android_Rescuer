package com.ncs.ims_rescuer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ncs.ims_rescuer.OAthManager.NaverOAthUtil
import com.ncs.ims_rescuer.databinding.ActivityLoginBinding
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler

class LoginActivity : AppCompatActivity() {
    lateinit var loginBinding: ActivityLoginBinding
    lateinit var oAuthLogin: OAuthLogin


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        init()
    }

    private fun init(){
        oAuthLogin = OAuthLogin.getInstance()
        var loginUtil = NaverOAthUtil()
        oAuthLogin.init(this, loginUtil.OAUTH_CLIENT_ID, loginUtil.OAUTH_CLIENT_SECRET, loginUtil.OAUTH_CLIENT_NAME)
        loginBinding.naverLogin.setOAuthLoginHandler(oAuthLoginHandler)
    }

    private var oAuthLoginHandler = object : OAuthLoginHandler(){
        override fun run(state: Boolean) {
            if(state){
                var accessToken = oAuthLogin.getAccessToken(this@LoginActivity)
                var refreshToken = oAuthLogin.getRefreshToken(this@LoginActivity)
                var expireAt = oAuthLogin.getExpiresAt(this@LoginActivity)
                var tokenType = oAuthLogin.getTokenType(this@LoginActivity)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }else{
                var errorCode = oAuthLogin.getLastErrorCode(this@LoginActivity).code
                var errorDesc = oAuthLogin.getLastErrorDesc(this@LoginActivity)
                Toast.makeText(this@LoginActivity, "errorCode: $errorCode errorDesc: $errorDesc", Toast.LENGTH_SHORT).show()
            }
        }
    }
}