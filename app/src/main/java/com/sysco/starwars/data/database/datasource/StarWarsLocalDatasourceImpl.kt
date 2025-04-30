package com.sysco.starwars.data.database.datasource

import android.util.Log
import com.sysco.starwars.data.database.StarWarsDao
import com.sysco.starwars.data.model.entity.Planet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

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