package com.hihihihi.data.di

import android.content.Context
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.work.WorkManager
import androidx.work.Configuration

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
