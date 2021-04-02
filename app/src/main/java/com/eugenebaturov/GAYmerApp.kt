package com.eugenebaturov

import android.app.Application
import com.eugenebaturov.gaymer.DotaRepository

class GAYmerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        DotaRepository.initialize(this)
    }
}