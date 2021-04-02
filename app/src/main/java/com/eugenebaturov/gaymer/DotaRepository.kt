package com.eugenebaturov.gaymer

import android.content.Context
import com.eugenebaturov.gaymer.api.RetrofitInstance
import com.eugenebaturov.gaymer.model.entities.Hero
import retrofit2.Response

class DotaRepository private constructor(context: Context) {

    suspend fun getHeroList(): Response<List<Hero>> =
        RetrofitInstance.heroApi.getHeroList()

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