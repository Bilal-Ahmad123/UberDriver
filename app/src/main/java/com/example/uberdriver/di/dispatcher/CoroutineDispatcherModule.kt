package com.example.uberdriver.di.dispatcher

import com.example.uberdriver.core.dispatcher.IDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoroutineDispatcherModule {
    @Provides
    @Singleton
    fun provideDispatchers(): IDispatchers = AppDispatcher()
}

class AppDispatcher: IDispatchers {

    override val main = Dispatchers.Main

    override val io = Dispatchers.IO

    override val db = Dispatchers.IO

    override val computation = Dispatchers.Default
}
