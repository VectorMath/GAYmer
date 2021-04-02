package com.eugenebaturov.gaymer

import android.app.Application

class GAYmerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        DotaRepository.initialize(this)
    }
}