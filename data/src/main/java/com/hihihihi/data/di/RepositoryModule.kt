package com.hihihihi.data.di

import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.repotisoryimpl.UserBookRepositoryImpl
import com.hihihihi.domain.repository.UserBookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUserBookRepository(
        remoteDataSource: UserBookRemoteDataSource
    ): UserBookRepository {
        return UserBookRepositoryImpl(remoteDataSource)
    }
}