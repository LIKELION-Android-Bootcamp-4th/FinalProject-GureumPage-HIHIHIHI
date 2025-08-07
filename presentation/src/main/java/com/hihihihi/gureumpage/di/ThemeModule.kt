package com.hihihihi.gureumpage.di

import android.content.Context
import com.hihihihi.data.remote.datasource.ThemeRemoteDataSource
import com.hihihihi.data.remote.datasourceimpl.ThemeRemoteDataSourceImpl
import com.hihihihi.data.repotisoryimpl.ThemeRepositoryImpl
import com.hihihihi.domain.repository.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ThemeModule {

    // DataSource 제공
    @Provides
    @Singleton
    fun provideThemeRemoteDataSource(
        @ApplicationContext context: Context
    ): ThemeRemoteDataSource = ThemeRemoteDataSourceImpl(context)

    //Repository 제공
    @Provides
    @Singleton
    fun provideThemeRepository(
        dataSource: ThemeRemoteDataSource
    ): ThemeRepository = ThemeRepositoryImpl(dataSource)
}