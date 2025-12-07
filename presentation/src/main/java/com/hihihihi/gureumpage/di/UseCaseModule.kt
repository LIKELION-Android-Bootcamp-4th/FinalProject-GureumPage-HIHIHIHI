package com.hihihihi.gureumpage.di

import com.hihihihi.domain.repository.AuthRepository
import com.hihihihi.domain.repository.DailyReadPageRepository
import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.repository.KakaoAuthRepository
import com.hihihihi.domain.repository.MindmapNodeRepository
import com.hihihihi.domain.repository.MindmapRepository
import com.hihihihi.domain.repository.NaverAuthRepository
import com.hihihihi.domain.repository.QuoteRepository
import com.hihihihi.domain.repository.SearchRepository
import com.hihihihi.domain.repository.UserBookRepository
import com.hihihihi.domain.repository.UserPreferencesRepository
import com.hihihihi.domain.repository.UserRepository
import com.hihihihi.domain.usecase.auth.SignInWithGoogleUseCase
import com.hihihihi.domain.usecase.auth.SignInWithKakaoUseCase
import com.hihihihi.domain.usecase.auth.SignInWithNaverUseCase
import com.hihihihi.domain.usecase.daily.GetDailyReadPagesUseCase
import com.hihihihi.domain.usecase.mindmap.CreateMindmapUseCase
import com.hihihihi.domain.usecase.mindmap.GetMindmapUseCase
import com.hihihihi.domain.usecase.mindmap.UpdateMindmapUseCase
import com.hihihihi.domain.usecase.mindmapnode.ApplyNodeOperation
import com.hihihihi.domain.usecase.mindmapnode.LoadNodesUseCase
import com.hihihihi.domain.usecase.mindmapnode.ObserveUseCase
import com.hihihihi.domain.usecase.history.AddHistoryUseCase
import com.hihihihi.domain.usecase.quote.AddQuoteUseCase
import com.hihihihi.domain.usecase.quote.GetQuoteByUserBookIdUseCase
import com.hihihihi.domain.usecase.quote.GetQuoteUseCase
import com.hihihihi.domain.usecase.quote.UpdateQuoteUseCase
import com.hihihihi.domain.usecase.search.SearchBooksUseCase
import com.hihihihi.domain.usecase.statistics.GetStatisticsUseCase
import com.hihihihi.domain.usecase.user.CheckRecentVisitUseCase
import com.hihihihi.domain.usecase.user.ClearUserDataUseCase
import com.hihihihi.domain.usecase.user.GetHomeDataUseCase
import com.hihihihi.domain.usecase.user.GetLastProviderUseCase
import com.hihihihi.domain.usecase.user.GetLastVisitUseCase
import com.hihihihi.domain.usecase.user.GetMyPageDataUseCase
import com.hihihihi.domain.usecase.user.GetNicknameFlowUseCase
import com.hihihihi.domain.usecase.user.GetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.GetThemeFlowUseCase
import com.hihihihi.domain.usecase.user.GetUserUseCase
import com.hihihihi.domain.usecase.user.SetLastProviderUseCase
import com.hihihihi.domain.usecase.user.SetNicknameUseCase
import com.hihihihi.domain.usecase.user.SetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.SetThemeUseCase
import com.hihihihi.domain.usecase.user.UpdateDailyGoalTimeUseCase
import com.hihihihi.domain.usecase.user.UpdateLastVisitUseCase
import com.hihihihi.domain.usecase.user.UpdateNicknameUseCase
import com.hihihihi.domain.usecase.userbook.AddUserBookUseCase
import com.hihihihi.domain.usecase.userbook.GetBookDetailDataUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBookByIdUseCase
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
    fun provideGetUserBookByIdUseCase(
        repository: UserBookRepository
    ): GetUserBookByIdUseCase {
        return GetUserBookByIdUseCase(repository)
    }

    @Provides
    fun provideGetHomeDataUseCase(
        userBookRepository: UserBookRepository,
        quoteRepository: QuoteRepository,
        historyRepository: HistoryRepository,
        userRepository: UserRepository
    ): GetHomeDataUseCase {
        return GetHomeDataUseCase(
            userBookRepository,
            quoteRepository,
            historyRepository,
            userRepository
        )
    }

    @Provides
    fun provideGetMyPageDataUseCase(
        userRepository: UserRepository,
        dailyRepository: DailyReadPageRepository,
        userBookRepository: UserBookRepository
    ): GetMyPageDataUseCase {
        return GetMyPageDataUseCase(userRepository, dailyRepository, userBookRepository)
    }

    @Provides
    fun provideGetUserBookUseCase(
        userBookRepository: UserBookRepository, // Repository가 자동 주입됨
        quoteRepository: QuoteRepository,
        historyRepository: HistoryRepository
    ): GetBookDetailDataUseCase {
        return GetBookDetailDataUseCase(
            userBookRepository,
            quoteRepository,
            historyRepository
        ) // UseCase 생성 후 반환
    }

    @Provides
    fun providePatchUserBookUseCase(
        repository: UserBookRepository
    ): PatchUserBookUseCase {
        return PatchUserBookUseCase(repository)
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
    fun provideAddHistory(
        historyRepository: HistoryRepository,
    ): AddHistoryUseCase {
        return AddHistoryUseCase(historyRepository)
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
    fun provideGetDailyReadPagesUseCase(
        repository: DailyReadPageRepository
    ): GetDailyReadPagesUseCase {
        return GetDailyReadPagesUseCase(repository)
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
    fun provideGetOnboardingCompleteUseCase(
        repository: UserPreferencesRepository
    ): GetOnboardingCompleteUseCase {
        return GetOnboardingCompleteUseCase(repository)
    }

    @Provides
    fun provideUpdateDailyGoalTimeUseCase(
        repository: UserRepository
    ): UpdateDailyGoalTimeUseCase {
        return UpdateDailyGoalTimeUseCase(repository)
    }

    @Provides
    fun provideUpdateNicknameUseCase(
        repository: UserRepository
    ): UpdateNicknameUseCase {
        return UpdateNicknameUseCase(repository)
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

    @Provides
    fun provideSetLastProviderUseCase(
        repository: UserPreferencesRepository
    ): SetLastProviderUseCase {
        return SetLastProviderUseCase(repository)
    }

    @Provides
    fun provideGetLastProviderUseCase(
        repository: UserPreferencesRepository
    ): GetLastProviderUseCase {
        return GetLastProviderUseCase(repository)
    }


    @Provides
    fun provideGetStatisticsUseCase(
        userBookRepository: UserBookRepository,
        historyRepository: HistoryRepository,
        dailyReadPageRepository: DailyReadPageRepository
    ): GetStatisticsUseCase {
        return GetStatisticsUseCase(userBookRepository, historyRepository, dailyReadPageRepository)
    }

    @Provides
    fun provideSearchBooksUseCase(
        repository: SearchRepository
    ): SearchBooksUseCase {
        return SearchBooksUseCase(repository)
    }

    @Provides
    fun provideClearUserDataUseCase(
        repository: UserPreferencesRepository
    ): ClearUserDataUseCase {
        return ClearUserDataUseCase(repository)
    }

    @Provides
    fun provideGetUserUseCase(
        repository: UserRepository
    ): GetUserUseCase {
        return GetUserUseCase(repository)
    }

    @Provides
    fun provideGetQuoteByUserBookIdUseCase(
        repository: QuoteRepository
    ): GetQuoteByUserBookIdUseCase {
        return GetQuoteByUserBookIdUseCase(repository)
    }

    @Provides
    fun provideUpdateQuoteUseCase(
        repository: QuoteRepository
    ): UpdateQuoteUseCase {
        return UpdateQuoteUseCase(repository)
    }

    @Provides
    fun provideGetLastVisitUseCase(
        repository: UserPreferencesRepository
    ): GetLastVisitUseCase {
        return GetLastVisitUseCase(repository)
    }

    @Provides
    fun provideUpdateLastVisitUseCase(
        repository: UserPreferencesRepository
    ): UpdateLastVisitUseCase {
        return UpdateLastVisitUseCase(repository)
    }

    @Provides
    fun provideCheckRecentVisitUseCase(
        repository: UserPreferencesRepository
    ): CheckRecentVisitUseCase {
        return CheckRecentVisitUseCase(repository)
    }
}
