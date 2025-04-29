package com.sysco.starwars.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.sysco.starwars.BuildConfig
import com.sysco.starwars.data.database.StarWarsDatabase
import com.sysco.starwars.data.database.StarWarsLocalDataSourceImpl
import com.sysco.starwars.data.database.StarWarsLocalDatasource
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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StarWarsModule {
    @Provides
    @Singleton
    fun provideStarWarsApi() : StarWarsApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_STARWARS)
            .client(
                OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StarWarsApi::class.java)
    }

    @Provides
    @Singleton
    fun providePicsumRandomImage() : PicsumApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_PICSUM)
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

    @Provides
    @Singleton
    fun provideStarWarsDatabase(@ApplicationContext context: Context) : StarWarsDatabase {
        return Room.databaseBuilder(
            context,
            StarWarsDatabase::class.java, "star-wars-database",
        ).setQueryCallback({ sqlQuery, bindArgs ->
            Log.d("RoomQuery", "SQL: $sqlQuery\nArgs: $bindArgs")
        }, Executors.newSingleThreadExecutor()).build()
    }

    @Provides
    @Singleton
    fun provideStarWarsDatasource(database: StarWarsDatabase): StarWarsLocalDatasource {
        return StarWarsLocalDataSourceImpl(database.starWarsDao())
    }
}