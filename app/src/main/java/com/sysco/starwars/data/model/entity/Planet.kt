package com.sysco.starwars.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planets")
data class Planet (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String?,
    val climate: String?,
    val orbitalPeriod: String?,
    val gravity: String?,
    val rotationPeriod: String?,
    var imageUrl: String?,
)