package com.eugenebaturov.gaymer

import android.content.Context
import com.eugenebaturov.gaymer.api.HeroApi
import com.eugenebaturov.gaymer.api.TeamApi
import com.eugenebaturov.gaymer.model.entities.Hero
import com.eugenebaturov.gaymer.model.entities.Team
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DotaRepository private constructor(context: Context) {

    // Build Retrofit
    private val retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.opendota.com/api")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Build Room Database


    // API
    val heroApi: HeroApi = retrofit.create(HeroApi::class.java)
    val teamApi: TeamApi = retrofit.create(TeamApi::class.java)

    suspend fun getHeroList(): Response<List<Hero>> = heroApi.getHeroList()

    suspend fun getTeamList(): Response<List<Team>> = teamApi.getTeamList()
    suspend fun getTeam(id: Int): Response<Team> = teamApi.getTeam(id)

    // DAO

    companion object {

        private var INSTANCE: DotaRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) INSTANCE = DotaRepository(context)
        }

        fun get(): DotaRepository {
            return INSTANCE ?: throw IllegalStateException("DotaRepository must be initialized")
        }
    }
}