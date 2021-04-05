package com.eugenebaturov.gaymer.model.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.eugenebaturov.gaymer.model.entities.LocalHero

@Dao
interface HeroDao {

    @Query("SELECT * FROM heroes")
    fun getHeroes(): LiveData<List<LocalHero>>

    @Query("SELECT * FROM heroes WHERE id=(:id)")
    fun getHeroById(id: Int): LiveData<LocalHero?>

    @Insert
    fun addHero(hero: LocalHero)

    @Delete
    fun removeHero(hero: LocalHero)
}