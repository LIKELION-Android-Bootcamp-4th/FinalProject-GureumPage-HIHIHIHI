package com.hihihihi.data.di

import com.hihihihi.data.notification.PushTokenRegistrarImpl
import com.hihihihi.domain.notification.PushTokenRegistrar
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PushBindModule {
    @Binds @Singleton
    abstract fun bindRegistrar(impl: PushTokenRegistrarImpl): PushTokenRegistrar
}