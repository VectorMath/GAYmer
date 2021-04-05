package com.eugenebaturov.gaymer.model.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eugenebaturov.gaymer.model.entities.LocalHero

@Dao
interface HeroDao {

    @Query("SELECT * FROM heroes")
    fun getHeroes(): LiveData<List<LocalHero>>

    @Query("SELECT * FROM heroes WHERE id=(:id)")
    fun getHeroById(id: Int): LiveData<LocalHero?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addHero(hero: LocalHero)

    @Delete
    fun removeHero(hero: LocalHero)
}