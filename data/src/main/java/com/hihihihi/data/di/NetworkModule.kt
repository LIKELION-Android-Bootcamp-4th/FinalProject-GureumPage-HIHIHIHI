package com.hihihihi.data.di

import com.hihihihi.data.common.utils.NetworkManager
import com.hihihihi.domain.repository.NetworkMonitor
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
    abstract fun bindNetworkMonitor(networkManager: NetworkManager): NetworkMonitor
}
