package com.example.uberdriver.di.database

import android.content.Context
import androidx.room.Room
import com.example.uberdriver.core.common.Constants_Api
import com.example.uberdriver.data.local.driver.dao.DriverDao
import com.example.uberdriver.data.local.driver.database.AppDatabase
import com.example.uberdriver.data.local.driver.repository.DriverRoomRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context:Context):AppDatabase{
        return Room.databaseBuilder(context,AppDatabase::class.java,Constants_Api.DB_NAME).build()
    }

    @Provides
    @Singleton
    fun provideDriverDao(appDatabase: AppDatabase) = appDatabase.getDriverDao()

    @Provides
    @Singleton
    fun provideDriverRoomRepository(driverDao:DriverDao):DriverRoomRepository{
        return com.example.uberdriver.domain.local.Driver.repository.DriverRoomRepository(driverDao)
    }
}