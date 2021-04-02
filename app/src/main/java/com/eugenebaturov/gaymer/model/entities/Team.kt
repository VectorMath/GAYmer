package com.eugenebaturov.gaymer.model.entities

import com.google.gson.annotations.SerializedName

data class Team(

    @SerializedName(value = "team_id")
    val id: Int,

    @SerializedName(value = "rating")
    val rating: Double,

    @SerializedName(value = "wins")
    val winsMatch: Int,

    @SerializedName(value = "losses")
    val loseMatch: Int,

    @SerializedName(value = "name")
    val teamName: String,

    @SerializedName(value = "logo_url")
    val logoUrl: String
)