package com.hihihihi.data.di

import com.hihihihi.data.repository.AuthRepositoryImpl
import com.hihihihi.data.repository.DailyReadPageRepositoryImpl
import com.hihihihi.data.repository.HistoryRepositoryImpl
import com.hihihihi.data.repository.MindmapNodeRepositoryImpl
import com.hihihihi.data.repository.MindmapRepositoryImpl
import com.hihihihi.data.repository.NotificationPreferencesRepositoryImpl
import com.hihihihi.data.repository.QuoteRepositoryImpl
import com.hihihihi.data.repository.SearchRepositoryImpl
import com.hihihihi.data.repository.UserBookRepositoryImpl
import com.hihihihi.data.repository.UserPreferencesRepositoryImpl
import com.hihihihi.data.repository.UserRepositoryImpl
import com.hihihihi.domain.repository.AuthRepository
import com.hihihihi.domain.repository.DailyReadPageRepository
import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.repository.MindmapNodeRepository
import com.hihihihi.domain.repository.MindmapRepository
import com.hihihihi.domain.repository.NotificationPreferencesRepository
import com.hihihihi.domain.repository.QuoteRepository
import com.hihihihi.domain.repository.SearchRepository
import com.hihihihi.domain.repository.UserBookRepository
import com.hihihihi.domain.repository.UserPreferencesRepository
import com.hihihihi.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserBookRepository(impl: UserBookRepositoryImpl): UserBookRepository

    @Singleton
    @Binds
    abstract fun bindQuoteRepository(impl: QuoteRepositoryImpl): QuoteRepository

    @Singleton
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindDailyReadPageRepository(impl: DailyReadPageRepositoryImpl): DailyReadPageRepository

    @Singleton
    @Binds
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Singleton
    @Binds
    abstract fun bindMindmapRepository(impl: MindmapRepositoryImpl): MindmapRepository

    @Singleton
    @Binds
    abstract fun bindMindmapNodeRepository(impl: MindmapNodeRepositoryImpl): MindmapNodeRepository

    @Singleton
    @Binds
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Singleton
    @Binds
    abstract fun bindNotificationPreferencesRepository(impl: NotificationPreferencesRepositoryImpl): NotificationPreferencesRepository
}
