package com.sysco.starwars.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sysco.starwars.data.model.Planet

@Dao
interface StarWarsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanetList(planetList: List<Planet>)

    @Query("SELECT * FROM planets")
    suspend fun getPlanetList(): List<Planet>
}