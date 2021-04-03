package com.eugenebaturov.gaymer

import android.content.Context
import com.eugenebaturov.gaymer.api.RetrofitInstance
import com.eugenebaturov.gaymer.model.entities.Hero
import com.eugenebaturov.gaymer.model.entities.Team
import com.eugenebaturov.gaymer.utils.Constants.Companion.REPOSITORY_ERROR_MESSAGE
import retrofit2.Response

class DotaRepository private constructor(context: Context) {

    suspend fun getHeroList(): Response<List<Hero>> =
        RetrofitInstance.heroApi.getHeroList()

    suspend fun getTeamList(): Response<List<Team>> =
        RetrofitInstance.teamApi.getTeamList()

    suspend fun getTeam(id: Int): Response<Team> =
        RetrofitInstance.teamApi.getTeam(id)

    companion object {
        private var INSTANCE: DotaRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) INSTANCE = DotaRepository(context)
        }

        fun get(): DotaRepository {
            return INSTANCE ?: throw IllegalStateException(REPOSITORY_ERROR_MESSAGE)
        }
    }
}