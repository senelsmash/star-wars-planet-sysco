package com.sysco.starwars.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sysco.starwars.BuildConfig
import com.sysco.starwars.data.model.Planet

@Database(entities = [Planet::class], version = BuildConfig.DATABASE_VERSION)
abstract class StarWarsDatabase : RoomDatabase() {
    abstract fun starWarsDao(): StarWarsDao
}