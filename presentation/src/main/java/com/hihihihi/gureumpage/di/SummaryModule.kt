package com.hihihihi.gureumpage.di

import com.hihihihi.gureumpage.notification.summary.SummaryProvider
import com.hihihihi.gureumpage.notification.summary.SummaryProviderImpl
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