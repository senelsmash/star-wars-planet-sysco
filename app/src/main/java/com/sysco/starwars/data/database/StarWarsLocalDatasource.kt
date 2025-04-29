package com.sysco.starwars.data.database

import android.util.Log
import com.sysco.starwars.data.model.Planet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface StarWarsLocalDatasource {
    suspend fun addPlanetList(planetList: List<Planet>)
    suspend fun getPlanetList(): List<Planet>
}

class StarWarsLocalDataSourceImpl @Inject constructor(
    private val starWarsDao: StarWarsDao
) : StarWarsLocalDatasource {
    private val TAG: String = StarWarsLocalDataSourceImpl::class.java.simpleName
    override suspend fun addPlanetList(planetList: List<Planet>) {
        Log.d(TAG,"addPlanetList to database ----> ${planetList.size}" )
        withContext(Dispatchers.IO) {
            starWarsDao.insertPlanetList(planetList)
        }
    }

    override suspend fun getPlanetList(): List<Planet> {
        Log.d(TAG,"getPlanetList from database ---->" )
        return withContext(Dispatchers.IO) {
            starWarsDao.getPlanetList()
        }
    }
}