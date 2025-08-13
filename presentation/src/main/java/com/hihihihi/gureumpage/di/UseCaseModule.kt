package com.hihihihi.gureumpage.di

import com.hihihihi.domain.repository.AuthRepository
import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.repository.KakaoAuthRepository
import com.hihihihi.domain.repository.NaverAuthRepository
import com.hihihihi.domain.repository.QuoteRepository
import com.hihihihi.domain.repository.UserBookRepository
import com.hihihihi.domain.repository.UserPreferencesRepository
import com.hihihihi.domain.usecase.auth.SignInWithGoogleUseCase
import com.hihihihi.domain.usecase.auth.SignInWithKakaoUseCase
import com.hihihihi.domain.usecase.auth.SignInWithNaverUseCase
import com.hihihihi.domain.usecase.quote.AddQuoteUseCase
import com.hihihihi.domain.usecase.quote.GetQuoteUseCase
import com.hihihihi.domain.usecase.user.GetNicknameFlowUseCase
import com.hihihihi.domain.usecase.user.GetThemeFlowUseCase
import com.hihihihi.domain.usecase.user.SetNicknameUseCase
import com.hihihihi.domain.usecase.user.SetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.SetThemeUseCase
import com.hihihihi.domain.usecase.userbook.AddUserBookUseCase
import com.hihihihi.domain.usecase.userbook.GetBookDetailDataUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBooksByStatusUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBooksUseCase
import com.hihihihi.domain.usecase.userbook.PatchUserBookUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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

    @Provides
    fun provideGetUserBooksByStatusUseCase(
        repository: UserBookRepository // Repository가 자동 주입됨
    ): GetUserBooksByStatusUseCase {
        return GetUserBooksByStatusUseCase(repository) // UseCase 생성 후 반환
    }

    @Provides
    fun provideGetUserBookUseCase(
        userBookRepository: UserBookRepository, // Repository가 자동 주입됨
        quoteRepository: QuoteRepository,
        historyRepository: HistoryRepository
    ): GetBookDetailDataUseCase {
        return GetBookDetailDataUseCase(userBookRepository,quoteRepository,historyRepository) // UseCase 생성 후 반환
    }

    @Provides
    fun providePatchUserBookUseCase(
        repository: UserBookRepository
    ): PatchUserBookUseCase {
        return PatchUserBookUseCase(repository)
    }
    @Provides
    fun provideAddUserBookUseCase(
        repository: UserBookRepository
    ): AddUserBookUseCase {
        return AddUserBookUseCase(repository)
    }

    // Quote 관련 UseCase를 DI로 주입하는 함수
    @Provides
    fun provideAddQuotesUseCase(
        repository: QuoteRepository
    ): AddQuoteUseCase {
        return AddQuoteUseCase(repository)
    }

    @Provides
    fun provideGetQuotesUseCase(
        repository: QuoteRepository
    ): GetQuoteUseCase {
        return GetQuoteUseCase(repository)
    }

    @Provides
    fun provideSignInWithGoogleUseCase(
        repository: AuthRepository
    ): SignInWithGoogleUseCase {
        return SignInWithGoogleUseCase(repository)
    }

    @Provides
    fun provideSignInWithKakaoUseCase(
        kakaoAuthRepository: KakaoAuthRepository,
        authRepository: AuthRepository
    ): SignInWithKakaoUseCase {
        return SignInWithKakaoUseCase(kakaoAuthRepository, authRepository)
    }

    @Provides
    fun provideSignInWithNaverUseCase(
        naverAuthRepository: NaverAuthRepository,
        authRepository: AuthRepository
    ): SignInWithNaverUseCase {
        return SignInWithNaverUseCase(naverAuthRepository, authRepository)
    }

    @Provides
    fun provideOnboardingCompleteUseCase(
        repository: UserPreferencesRepository
    ): SetOnboardingCompleteUseCase {
        return SetOnboardingCompleteUseCase(repository)
    }

    @Provides
    fun provideSetNicknameUseCase(
        repository: UserPreferencesRepository
    ): SetNicknameUseCase {
        return SetNicknameUseCase(repository)
    }

    @Provides
    fun provideGetNicknameUseCase(
        repository: UserPreferencesRepository
    ): GetNicknameFlowUseCase {
        return GetNicknameFlowUseCase(repository)
    }

    @Provides
    fun provideSetThemeUseCase(
        repository: UserPreferencesRepository
    ): SetThemeUseCase {
        return SetThemeUseCase(repository)
    }

    @Provides
    fun provideGetThemeUseCase(
        repository: UserPreferencesRepository
    ): GetThemeFlowUseCase {
        return GetThemeFlowUseCase(repository)
    }
}
