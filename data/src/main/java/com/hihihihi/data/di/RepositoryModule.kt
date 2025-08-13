package com.hihihihi.data.di

import com.hihihihi.data.remote.datasource.AuthDataSource
import com.hihihihi.data.remote.datasource.DailyReadPageRemoteDataSource
import com.hihihihi.data.remote.datasource.HistoryRemoteDataSource
import com.hihihihi.data.remote.datasource.KakaoDataSource
import com.hihihihi.data.remote.datasource.MindmapNodeRemoteDataSource
import com.hihihihi.data.remote.datasource.MindmapRemoteDataSource
import com.hihihihi.data.remote.datasource.NaverDataSource
import com.hihihihi.data.remote.datasource.QuoteRemoteDataSource
import com.hihihihi.data.remote.datasource.SearchRemoteDataSource
import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.repotisoryimpl.AuthRepositoryImpl
import com.hihihihi.data.repotisoryimpl.KakaoAuthRepositoryImpl
import com.hihihihi.data.repotisoryimpl.NaverAuthRepositoryImpl
import com.hihihihi.data.remote.datasource.UserRemoteDataSource
import com.hihihihi.data.repotisoryimpl.DailyReadPageRepositoryImpl
import com.hihihihi.data.repotisoryimpl.HistoryRepositoryImpl
import com.hihihihi.data.repotisoryimpl.MindmapNodeRepositoryImpl
import com.hihihihi.data.repotisoryimpl.MindmapRepositoryImpl
import com.hihihihi.data.repotisoryimpl.QuoteRepositoryImpl
import com.hihihihi.data.repotisoryimpl.SearchRepositoryImpl
import com.hihihihi.data.repotisoryimpl.UserBookRepositoryImpl
import com.hihihihi.domain.repository.AuthRepository
import com.hihihihi.domain.repository.KakaoAuthRepository
import com.hihihihi.domain.repository.NaverAuthRepository
import com.hihihihi.data.repotisoryimpl.UserRepositoryImpl
import com.hihihihi.domain.repository.DailyReadPageRepository
import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.repository.MindmapNodeRepository
import com.hihihihi.domain.repository.MindmapRepository
import com.hihihihi.domain.repository.QuoteRepository
import com.hihihihi.domain.repository.SearchRepository
import com.hihihihi.domain.repository.UserBookRepository
import com.hihihihi.domain.repository.UserRepository
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
    fun provideHistoryRepository(
        remoteDataSource: HistoryRemoteDataSource
    ): HistoryRepository {
        return HistoryRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        remoteDataSource: UserRemoteDataSource
    ) : UserRepository {
        return UserRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideDailyReadPageRepository(
        remoteDataSource: DailyReadPageRemoteDataSource
    ): DailyReadPageRepository {
        return DailyReadPageRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        remoteDataSource: SearchRemoteDataSource
    ): SearchRepository {
        return SearchRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideMindmapRepository(
        remoteDataSource: MindmapRemoteDataSource
    ): MindmapRepository {
        return MindmapRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideMindmapNodeRepository(
        remoteDataSource: MindmapNodeRemoteDataSource
    ): MindmapNodeRepository {
        return MindmapNodeRepositoryImpl(remoteDataSource)
    }
}