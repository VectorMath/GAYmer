package com.eugenebaturov.gaymer.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eugenebaturov.gaymer.model.entities.LocalHero


@Database(entities = [LocalHero::class], version = 1)
abstract class HeroDatabase: RoomDatabase() {
    abstract fun heroDao(): HeroDao
}