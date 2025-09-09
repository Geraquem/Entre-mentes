package com.mmfsin.betweenminds.base

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
//import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        MobileAds.initialize(this) {}

        getFCMToken()
        disableNightMode()
    }

    private fun getFCMToken() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener {
//            if (it.isSuccessful) {
//                Log.i("**** FCM **** -> ", it.result)
//                println("**** FCM **** ${it.result}")
//            } else Log.e("FCM ->", "no token")
//        }
    }

    private fun disableNightMode() = setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
}