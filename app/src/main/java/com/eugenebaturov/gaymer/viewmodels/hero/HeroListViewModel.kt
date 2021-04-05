package com.eugenebaturov.gaymer.viewmodels.hero

import androidx.lifecycle.*
import com.eugenebaturov.gaymer.DotaRepository
import com.eugenebaturov.gaymer.model.entities.Hero
import com.eugenebaturov.gaymer.model.entities.LocalHero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

class HeroListViewModel(private val repository: DotaRepository): ViewModel() {

    val myResponse: MutableLiveData<Response<List<Hero>>> = MutableLiveData()

    // Coroutines
    fun getHeroes() {
        viewModelScope.launch {
            val response = repository.getHeroList()
            myResponse.value = response
        }
    }
}