package com.msa.msahub

import android.app.Application
import com.msa.msahub.app.AppInitializer

class MsaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.init(this)
    }
}
