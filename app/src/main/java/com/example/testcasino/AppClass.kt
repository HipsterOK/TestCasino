package com.example.testcasino

import android.app.Application
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal

class AppClass : Application() {
    companion object {
        const val appsKey = "YgFzfcdAJcavXYmABVDnDb"
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        OneSignal.initWithContext(this)
        OneSignal.setAppId("babc516b-39e5-4c33-9dc3-ceaa67e78956")
    }
}
