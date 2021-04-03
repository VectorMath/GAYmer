package com.eugenebaturov.gaymer.api

import com.eugenebaturov.gaymer.model.entities.Hero
import retrofit2.Response
import retrofit2.http.GET

interface HeroApi {

    @GET("HEROES")
    suspend fun getHeroList(): Response<List<Hero>>

}

//https://api.opendota.com/api/heroStats
//https://api.opendota.com/api/heroes