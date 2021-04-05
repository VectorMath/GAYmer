package com.eugenebaturov.gaymer.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "heroes")
data class LocalHero(

    @PrimaryKey
    var id: Int,

    var name: String,

    var primaryAttribute: String,

    var attackType: String,

    var roles: String,

    var urlImage: String,

    var urlIcon: String,

    var baseHealth: Int,

    var baseMana: Int,

    var baseArmor: Double,

    var minDamage: Int,

    var maxDamage: Int,

    var moveSpeed: Int,

    var str: Int,

    var agi: Int,

    var intHero: Int
)