package com.hihihihi.data.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.hihihihi.data.local.datasource.UserPreferencesLocalDataSource
import com.hihihihi.data.local.datasourceimpl.UserPreferencesLocalDataSourceImpl
import com.hihihihi.data.remote.datasource.AuthDataSource
import com.hihihihi.data.remote.datasource.DailyReadPageRemoteDataSource
import com.hihihihi.data.remote.datasource.HistoryRemoteDataSource
import com.hihihihi.data.remote.datasource.KakaoDataSource
import com.hihihihi.data.remote.datasource.NaverDataSource
import com.hihihihi.data.remote.datasource.QuoteRemoteDataSource
import com.hihihihi.data.remote.datasource.SearchRemoteDataSource
import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.remote.datasource.UserRemoteDataSource
import com.hihihihi.data.remote.datasourceimpl.AuthDataSourceImpl
import com.hihihihi.data.remote.datasourceimpl.DailyReadPageRemoteDataSourceImpl
import com.hihihihi.data.remote.datasourceimpl.HistoryRemoteDataSourceImpl
import com.hihihihi.data.remote.datasourceimpl.KakaoDataSourceImpl
import com.hihihihi.data.remote.datasourceimpl.NaverDataSourceImpl
import com.hihihihi.data.remote.datasourceimpl.QuoteRemoteDataSourceImpl
import com.hihihihi.data.remote.datasourceimpl.SearchRemoteDataSourceImpl
import com.hihihihi.data.remote.datasourceimpl.UserBookRemoteDataSourceImpl
import com.hihihihi.data.remote.datasourceimpl.UserRemoteDataSourceImpl
import com.hihihihi.data.remote.service.SearchApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    // UserBookRemoteDataSource 의존성 주입 제공
    @Provides
    @Singleton
    fun provideUserBookRemoteDataSource(
        firestore: FirebaseFirestore // Firestore 인스턴스 주입
    ): UserBookRemoteDataSource {
        return UserBookRemoteDataSourceImpl(firestore) // 구현체 반환
    }

    @Provides
    @Singleton
    fun provideQuoteRemoteDataSource(
        firestore: FirebaseFirestore
    ): QuoteRemoteDataSource {
        return QuoteRemoteDataSourceImpl(firestore)
    }

    @Provides
    @Singleton
    fun privateAuthDataSource(
        auth: FirebaseAuth,
        functions: FirebaseFunctions
    ): AuthDataSource {
       return AuthDataSourceImpl(auth, functions)
    }

    @Provides
    @Singleton
    fun provideNaverAuthDataSource(
    ): NaverDataSource {
        return NaverDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideKakaoDataSource(
        @ApplicationContext context: Context
    ): KakaoDataSource {
        return KakaoDataSourceImpl(context)
    }

    @Provides
    @Singleton
    fun provideHistoryRemotedDataSource(
        firestore: FirebaseFirestore
    ): HistoryRemoteDataSource {
        return HistoryRemoteDataSourceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(
        firestore: FirebaseFirestore
    ): UserRemoteDataSource {
        return UserRemoteDataSourceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideDailyReadPageRemoteDataSource(
        firestore: FirebaseFirestore
    ): DailyReadPageRemoteDataSource {
        return DailyReadPageRemoteDataSourceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideSearchRemoteDataSource(
        searchApiService: SearchApiService,
        apiKey: String
    ): SearchRemoteDataSource {
        return SearchRemoteDataSourceImpl(searchApiService, apiKey)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesLocalDataSource(
        @ApplicationContext context: Context
    ): UserPreferencesLocalDataSource {
        return UserPreferencesLocalDataSourceImpl(context)
    }
}