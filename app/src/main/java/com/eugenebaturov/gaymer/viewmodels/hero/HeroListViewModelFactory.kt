package com.eugenebaturov.gaymer.viewmodels.hero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eugenebaturov.gaymer.DotaRepository

class HeroListViewModelFactory(
    private val repository: DotaRepository): ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HeroListViewModel(repository) as T
    }
}