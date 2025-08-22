package com.hihihihi.gureumpage.di

import android.content.Context
import com.hihihihi.gureumpage.notification.NotificationFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Provides
    @Singleton
    fun provideNotificationFactory(@ApplicationContext context: Context) = NotificationFactory(context)
}