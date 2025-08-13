package com.hihihihi.gureumpage.di

import com.hihihihi.domain.repository.AuthRepository
import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.repository.KakaoAuthRepository
import com.hihihihi.domain.repository.MindmapNodeRepository
import com.hihihihi.domain.repository.MindmapRepository
import com.hihihihi.domain.repository.NaverAuthRepository
import com.hihihihi.domain.repository.QuoteRepository
import com.hihihihi.domain.repository.UserBookRepository
import com.hihihihi.domain.repository.UserPreferencesRepository
import com.hihihihi.domain.usecase.auth.SignInWithGoogleUseCase
import com.hihihihi.domain.usecase.auth.SignInWithKakaoUseCase
import com.hihihihi.domain.usecase.auth.SignInWithNaverUseCase
import com.hihihihi.domain.usecase.mindmap.CreateMindmapUseCase
import com.hihihihi.domain.usecase.mindmap.GetMindmapUseCase
import com.hihihihi.domain.usecase.mindmap.UpdateMindmapUseCase
import com.hihihihi.domain.usecase.mindmapnode.ApplyNodeOperation
import com.hihihihi.domain.usecase.mindmapnode.LoadNodesUseCase
import com.hihihihi.domain.usecase.mindmapnode.ObserveUseCase
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
    fun provideGetBookDetailDataUseCase(
        userBookRepository: UserBookRepository, // Repository가 자동 주입됨
        quoteRepository: QuoteRepository,
        historyRepository: HistoryRepository
    ): GetBookDetailDataUseCase {
        return GetBookDetailDataUseCase(userBookRepository,quoteRepository,historyRepository) // UseCase 생성 후 반환
    }

    @Provides
    fun provideAddUserBookUseCase(
        userBookRepository: UserBookRepository,
        mindmapRepository: MindmapRepository,
        mindmapNodeRepository: MindmapNodeRepository,
    ): AddUserBookUseCase {
        return AddUserBookUseCase(
            userBookRepository = userBookRepository,
            mindmapRepository = mindmapRepository,
            mindmapNodeRepository = mindmapNodeRepository,
        )
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
    fun provideCreateMindmapUseCase(
        repository: MindmapRepository
    ): CreateMindmapUseCase {
        return CreateMindmapUseCase(repository)
    }

    @Provides
    fun provideGetMindmapUseCase(
        repository: MindmapRepository
    ): GetMindmapUseCase {
        return GetMindmapUseCase(repository)
    }

    @Provides
    fun provideUpdateMindmapUseCase(
        repository: MindmapRepository
    ): UpdateMindmapUseCase {
        return UpdateMindmapUseCase(repository)
    }

    @Provides
    fun provideObserveUseCase(
        repository: MindmapNodeRepository
    ): ObserveUseCase {
        return ObserveUseCase(repository)
    }

    @Provides
    fun provideLoadNodesUseCase(
        repository: MindmapNodeRepository
    ): LoadNodesUseCase {
        return LoadNodesUseCase(repository)
    }

    @Provides
    fun provideApplyNodeOperation(
        repository: MindmapNodeRepository
    ): ApplyNodeOperation {
        return ApplyNodeOperation(repository)
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
