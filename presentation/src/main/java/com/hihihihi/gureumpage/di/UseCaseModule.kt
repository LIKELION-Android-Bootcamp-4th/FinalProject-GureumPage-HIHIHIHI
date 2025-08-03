package com.hihihihi.gureumpage.di

import com.hihihihi.domain.repository.QuoteRepository
import com.hihihihi.domain.repository.UserBookRepository
import com.hihihihi.domain.usecase.quote.AddQuoteUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBooksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // Hilt 모듈 선언
@InstallIn(SingletonComponent::class) // 앱 전체 생명주기 동안 싱글톤으로 유지되는 컴포넌트에 설치
object UseCaseModule {

    // UserBook 관련 UseCase를 DI로 주입하는 함수
    @Provides
    fun provideGetUserBooksUseCase(
        repository: UserBookRepository // Repository가 자동 주입됨
    ): GetUserBooksUseCase {
        return GetUserBooksUseCase(repository) // UseCase 생성 후 반환
    }

    // Quote 관련 UseCase를 DI로 주입하는 함수
    @Provides
    fun provideAddQuotesUseCase(
        repository: QuoteRepository
    ): AddQuoteUseCase {
        return AddQuoteUseCase(repository)
    }
}