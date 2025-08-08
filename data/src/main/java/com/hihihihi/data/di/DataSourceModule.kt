package com.hihihihi.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.remote.datasource.HistoryRemoteDataSource
import com.hihihihi.data.remote.datasource.QuoteRemoteDataSource
import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.remote.datasourceimpl.HistoryRemoteDataSourceImpl
import com.hihihihi.data.remote.datasourceimpl.QuoteRemoteDataSourceImpl
import com.hihihihi.data.remote.datasourceimpl.UserBookRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideHistoryRemotedDataSource(
        firestore: FirebaseFirestore
    ): HistoryRemoteDataSource {
        return HistoryRemoteDataSourceImpl(firestore)
    }
}