package com.mmfsin.betweenminds.base

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        disableNightMode()
    }

    private fun disableNightMode() = setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
}