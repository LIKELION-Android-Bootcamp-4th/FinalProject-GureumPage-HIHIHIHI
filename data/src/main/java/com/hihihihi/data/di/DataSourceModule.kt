package com.hihihihi.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.remote.datasource.QuoteRemoteDataSource
import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
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

    @Provides
    @Singleton
    fun provideUserBookRemoteDataSource(
        firestore: FirebaseFirestore
    ): UserBookRemoteDataSource {
        return UserBookRemoteDataSourceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideQuoteRemoteDataSource(
        firestore: FirebaseFirestore
    ): QuoteRemoteDataSource {
        return QuoteRemoteDataSourceImpl(firestore)
    }



}