package com.shjeong.logintest.common

import android.app.Activity
import android.app.Application
import com.kakao.auth.KakaoSDK


class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        globalApplicationContext = this
        KakaoSDK.init(KakaoSDKAdapter())
    }

    companion object {
        var globalApplicationContext: GlobalApplication? = null
            private set
        // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
        @Volatile
        var currentActivity: Activity? = null
    }

}