package com.eugenebaturov.gaymer.viewmodels.hero

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.eugenebaturov.gaymer.DotaRepository
import com.eugenebaturov.gaymer.model.entities.LocalHero

class HeroViewModel(private val repository: DotaRepository) : ViewModel() {

    var allHeroes: LiveData<List<LocalHero>> = repository.getHeroes()

    fun insert(hero: LocalHero) {
        repository.addHero(hero)
    }

    fun remove(hero: LocalHero) {
        repository.removeHero(hero)
    }
}