package com.eugenebaturov.gaymer.viewmodels.team

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eugenebaturov.gaymer.DotaRepository
import com.eugenebaturov.gaymer.model.entities.Team
import kotlinx.coroutines.launch
import retrofit2.Response

class TeamListViewModel(private val repository: DotaRepository): ViewModel() {

    val myResponse: MutableLiveData<Response<List<Team>>> = MutableLiveData()

    fun getTeams() {
        viewModelScope.launch {
            val response = repository.getTeamList()
            myResponse.value = response
        }
    }
}