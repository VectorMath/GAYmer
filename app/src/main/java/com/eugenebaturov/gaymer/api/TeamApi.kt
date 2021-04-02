package com.eugenebaturov.gaymer.api

import com.eugenebaturov.gaymer.model.entities.Team
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TeamApi {

    @GET("teams")
    suspend fun getTeamList(): Response<List<Team>>

    @GET("teams/{id}")
    suspend fun getTeam(@Path("id")id: Int) : Response<Team>
}