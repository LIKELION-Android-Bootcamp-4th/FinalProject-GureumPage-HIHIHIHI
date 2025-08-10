package com.hihihihi.data.di

import com.hihihihi.data.remote.datasource.AuthDataSource
import com.hihihihi.data.remote.datasource.KakaoDataSource
import com.hihihihi.data.remote.datasource.NaverDataSource
import com.hihihihi.data.remote.datasource.QuoteRemoteDataSource
import com.hihihihi.data.remote.datasource.SearchRemoteDataSource
import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.repotisoryimpl.AuthRepositoryImpl
import com.hihihihi.data.repotisoryimpl.KakaoAuthRepositoryImpl
import com.hihihihi.data.repotisoryimpl.NaverAuthRepositoryImpl
import com.hihihihi.data.repotisoryimpl.QuoteRepositoryImpl
import com.hihihihi.data.repotisoryimpl.SearchRepositoryImpl
import com.hihihihi.data.repotisoryimpl.UserBookRepositoryImpl
import com.hihihihi.domain.repository.AuthRepository
import com.hihihihi.domain.repository.KakaoAuthRepository
import com.hihihihi.domain.repository.NaverAuthRepository
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
    fun provideAuthRepository(
        authDataSource: AuthDataSource
    ): AuthRepository {
       return AuthRepositoryImpl(authDataSource)
    }


    @Provides
    @Singleton
    fun provideNaverAuthRepository(
        naverDataSource: NaverDataSource
    ): NaverAuthRepository {
        return NaverAuthRepositoryImpl(naverDataSource)
    }

    @Provides
    @Singleton
    fun provideKakaoAuthRepository(
        kakaoDataSource: KakaoDataSource
    ): KakaoAuthRepository {
        return KakaoAuthRepositoryImpl(kakaoDataSource)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        remoteDataSource: SearchRemoteDataSource
    ): SearchRepository {
        return SearchRepositoryImpl(remoteDataSource)
    }
}