package com.rarestardev.taskora.application

import android.app.Application
import com.adivery.sdk.Adivery
import com.jakewharton.threetenabp.AndroidThreeTen
import com.rarestardev.taskora.utilities.Constants

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Adivery.configure(this, Constants.AD_APPLICATION_ID)
        Adivery.setLoggingEnabled(true)
    }
}