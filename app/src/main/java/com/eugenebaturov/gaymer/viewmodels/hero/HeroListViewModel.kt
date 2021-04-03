package com.eugenebaturov.gaymer.viewmodels.hero

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eugenebaturov.gaymer.DotaRepository
import com.eugenebaturov.gaymer.model.entities.Hero
import kotlinx.coroutines.launch
import retrofit2.Response

class HeroListViewModel(private val repository: DotaRepository): ViewModel() {

    val myResponse: MutableLiveData<Response<List<Hero>>> = MutableLiveData()

    fun getHeroes() {
        viewModelScope.launch {
            val response = repository.getHeroList()
            myResponse.value = response
        }
    }
}