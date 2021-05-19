package com.ncs.ims_rescuer.KakaoAPI

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.ncs.ims_rescuer.R


class GlobalApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, resources.getString(R.string.kakao_app_key))
    }
}