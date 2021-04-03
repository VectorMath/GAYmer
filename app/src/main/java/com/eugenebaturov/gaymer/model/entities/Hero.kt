package com.eugenebaturov.gaymer.model.entities

import com.google.gson.annotations.SerializedName

data class Hero(

    @SerializedName(value = "id")
    val id: Int,

    @SerializedName(value = "localized_name")
    val name: String,

    @SerializedName(value = "primary_attr")
    val primaryAttribute: String,

    @SerializedName(value = "attack_type")
    val attackType: String,

    @SerializedName(value = "roles")
    val roles: List<String>,

    @SerializedName(value = "img")
    val urlImage: String,

    @SerializedName(value = "icon")
    val urlIcon: String,

    @SerializedName(value = "base_health")
    val baseHealth: Int,

    @SerializedName(value = "base_mana")
    val baseMana: Int,

    @SerializedName(value = "base_armor")
    val baseArmor: Int,

    @SerializedName(value = "base_attack_min")
    val minDamage: Int,

    @SerializedName(value = "base_attack_max")
    val maxDamage: Int,

    @SerializedName(value = "move_speed")
    val moveSpeed: Int,

    @SerializedName(value = "base_str")
    var str: Int,

    @SerializedName(value = "base_agi")
    val agi: Int,

    @SerializedName(value = "base_int")
    val int: Int

)