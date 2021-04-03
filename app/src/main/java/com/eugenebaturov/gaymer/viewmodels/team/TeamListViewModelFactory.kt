package com.eugenebaturov.gaymer.viewmodels.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eugenebaturov.gaymer.DotaRepository

class TeamListViewModelFactory(
    private val repository: DotaRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TeamListViewModel(repository) as T
    }
}