package com.sysco.starwars.data.model.response

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "planets")
data class PlanetResponse(
    val count: Long,
    @SerializedName("next")
    val nextPageUrl: String?,
    val previous: Any?,
    @SerializedName("results")
    val planetList: List<PlanetWrapper>
)

data class PlanetData(
    val name: String?,
    val climate: String?,
    @SerializedName("orbital_period")
    val orbitalPeriod: String?,
    val gravity: String?,
    @SerializedName("rotation_period")
    val rotationPeriod: String?,
)

data class PlanetWrapper(
    val fields: PlanetData
)
