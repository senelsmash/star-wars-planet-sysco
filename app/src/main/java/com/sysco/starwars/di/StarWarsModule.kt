package com.sysco.starwars.di

import com.sysco.starwars.data.remote.PicsumApi
import com.sysco.starwars.data.remote.StarWarsApi
import com.sysco.starwars.data.remote.StarWarsApiDataSource
import com.sysco.starwars.data.remote.StarWarsApiDataSourceImpl
import com.sysco.starwars.data.repository.StarWarsRepository
import com.sysco.starwars.data.repository.StarWarsRepositoryImpl
import com.sysco.starwars.viewmodel.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StarWarsModule {
    @Provides
    @Singleton
    fun provideStarWarsApi() : StarWarsApi {
        return Retrofit.Builder()
            .baseUrl("https://swapi-node.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StarWarsApi::class.java)
    }

    @Provides
    @Singleton
    fun providePicsumRandomImage() : PicsumApi {
        return Retrofit.Builder()
            .baseUrl("https://picsum.photos/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicsumApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStarWarsApiDataSource(starWarsApiDataSourceImpl: StarWarsApiDataSourceImpl):
            StarWarsApiDataSource = starWarsApiDataSourceImpl

    @Provides
    @Singleton
    fun provideStarWarsRepository(starWarsRepositoryImpl: StarWarsRepositoryImpl):
            StarWarsRepository = starWarsRepositoryImpl

    @Provides
    @Singleton
    fun provideMainViewModel(starWarsRepository: StarWarsRepository): MainViewModel {
        return MainViewModel(starWarsRepository)
    }
}