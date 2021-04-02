package com.eugenebaturov.gaymer.api

import com.eugenebaturov.gaymer.model.entities.Hero
import retrofit2.Response
import retrofit2.http.GET

interface HeroApi {

    @GET("heroes")
    suspend fun getHeroList(): Response<List<Hero>>

}