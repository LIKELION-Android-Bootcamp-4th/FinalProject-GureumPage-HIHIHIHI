package com.hihihihi.gureumpage.di

import com.hihihihi.domain.repository.UserBookRepository
import com.hihihihi.domain.usecase.userbook.GetUserBooksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetUserBooksUseCase(
        repository: UserBookRepository
    ): GetUserBooksUseCase {
        return GetUserBooksUseCase(repository)
    }
}