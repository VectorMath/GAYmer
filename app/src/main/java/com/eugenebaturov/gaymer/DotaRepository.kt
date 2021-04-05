package com.eugenebaturov.gaymer

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.eugenebaturov.gaymer.api.RetrofitInstance
import com.eugenebaturov.gaymer.model.entities.Hero
import com.eugenebaturov.gaymer.model.entities.LocalHero
import com.eugenebaturov.gaymer.model.entities.Team
import com.eugenebaturov.gaymer.model.local.HeroDatabase
import com.eugenebaturov.gaymer.utils.Constants.Companion.DATABASE_NAME
import com.eugenebaturov.gaymer.utils.Constants.Companion.REPOSITORY_ERROR_MESSAGE
import retrofit2.Response
import java.util.concurrent.Executors

class DotaRepository private constructor(context: Context) {

    // Room DB
    private val database: HeroDatabase = Room.databaseBuilder(
        context.applicationContext,
        HeroDatabase::class.java,
        DATABASE_NAME
    ).build()


    private val heroDao = database.heroDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getHeroes(): LiveData<List<LocalHero>> = heroDao.getHeroes()
    fun getHero(id: Int): LiveData<LocalHero?> = heroDao.getHeroById(id)

    fun addHero(hero: LocalHero) {
        executor.execute {
            heroDao.addHero(hero)
        }
    }

    fun removeHero(hero: LocalHero) {
        executor.execute {
            heroDao.removeHero(hero)
        }
    }


    // Retrofit
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