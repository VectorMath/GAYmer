package com.eugenebaturov.gaymer.viewmodels.hero

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eugenebaturov.gaymer.DotaRepository

class HeroViewModelFactory(
    private val repository: DotaRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HeroViewModel(repository) as T
    }
}