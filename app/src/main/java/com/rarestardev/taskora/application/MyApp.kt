package com.rarestardev.taskora.application

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}