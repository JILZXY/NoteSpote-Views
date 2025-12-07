package com.example.notespote.di

import com.example.notespote.data.network.NetworkMonitor
import com.example.notespote.data.network.NetworkMonitorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        networkMonitorImpl: NetworkMonitorImpl
    ): NetworkMonitor
}
