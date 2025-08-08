package com.hihihihi.data.di

import com.hihihihi.data.remote.datasource.QuoteRemoteDataSource
import com.hihihihi.data.remote.datasource.SearchRemoteDataSource
import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.repotisoryimpl.QuoteRepositoryImpl
import com.hihihihi.data.repotisoryimpl.SearchRepositoryImpl
import com.hihihihi.data.repotisoryimpl.UserBookRepositoryImpl
import com.hihihihi.domain.repository.QuoteRepository
import com.hihihihi.domain.repository.SearchRepository
import com.hihihihi.domain.repository.UserBookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)  // 앱 전체 라이프사이클 동안 싱글톤으로 제공
object RepositoryModule {
    // UserBookRepository 의존성 주입 제공
    @Provides
    @Singleton
    fun provideUserBookRepository(
        remoteDataSource: UserBookRemoteDataSource // 원격 데이터 소스 주입
    ): UserBookRepository {
        return UserBookRepositoryImpl(remoteDataSource) // 구현체 반환
    }

    @Provides
    @Singleton
    fun provideQuoteRepository(
        remoteDataSource: QuoteRemoteDataSource
    ): QuoteRepository {
        return QuoteRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        remoteDataSource: SearchRemoteDataSource
    ): SearchRepository {
        return SearchRepositoryImpl(remoteDataSource)
    }
}