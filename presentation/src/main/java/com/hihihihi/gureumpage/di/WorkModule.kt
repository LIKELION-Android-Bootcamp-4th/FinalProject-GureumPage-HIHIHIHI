package com.hihihihi.gureumpage.di

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkModule {
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext ctx: Context): WorkManager {
        // WorkManager 초기화 확인
        return try {
            WorkManager.getInstance(ctx)
        } catch (e: Exception) {
            // 기본 설정으로 초기화
            val config = Configuration.Builder()
                .setMinimumLoggingLevel(Log.INFO)
                .build()
            WorkManager.initialize(ctx, config)
            WorkManager.getInstance(ctx)
        }
    }
}