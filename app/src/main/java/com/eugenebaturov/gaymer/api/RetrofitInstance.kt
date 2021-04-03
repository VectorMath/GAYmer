package com.eugenebaturov.gaymer.api

import com.eugenebaturov.gaymer.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val heroApi : HeroApi by lazy {
        retrofit.create(HeroApi::class.java)
    }

    val teamApi: TeamApi by lazy {
        retrofit.create(TeamApi::class.java)
    }
}