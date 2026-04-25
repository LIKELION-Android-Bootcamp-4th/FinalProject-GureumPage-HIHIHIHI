package com.hihihihi.presentation.di

import com.hihihihi.presentation.notification.summary.SummaryProvider
import com.hihihihi.presentation.notification.summary.SummaryProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SummaryModule {

    @Binds
    abstract fun bindSummaryProvider(impl: SummaryProviderImpl): SummaryProvider
}
