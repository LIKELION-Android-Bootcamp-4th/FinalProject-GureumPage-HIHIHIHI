package com.hihihihi.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.repository.NetworkMonitor
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.notification.GetNotificationSettingsUseCase
import com.hihihihi.domain.usecase.user.GetUserUseCase
import com.hihihihi.domain.usecase.user.SetOnboardingCompleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val setOnboardingCompleteUseCase: SetOnboardingCompleteUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val networkManager: NetworkMonitor,
) : ViewModel() {

    sealed interface NavTarget {
        data object Loading : NavTarget
        data object Login : NavTarget
        data object Onboarding : NavTarget
        data object Home : NavTarget
        data object NoNetwork : NavTarget
        data class Widget(val route: Any) : NavTarget
    }

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Content())
    val uiState: StateFlow<SplashUiState> = _uiState

    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect: Flow<SplashEffect> = _effect.receiveAsFlow()

    private var pendingWidgetRoute: Any? = null
    private var didSendNavigation = false

    fun setPendingWidgetRoute(route: Any?) {
        pendingWidgetRoute = route
    }

    fun markPermissionAsked() {
        updateContent { it.copy(permissionAsked = true) }
    }

    fun onPermissionResult() {
        updateContent { it.copy(permissionHandled = true) }
        emitNavigationIfReady()
    }

    fun markSchedulersSetUp() {
        updateContent { it.copy(schedulersSetUp = true) }
    }

    suspend fun getNotificationSettings() = getNotificationSettingsUseCase().first()

    fun checkNetworkAndProceed() {
        viewModelScope.launch {
            updateContent { it.copy(loadingMessage = "구름한장을 시작하는 중...", progress = 0.2f) }
            delay(400)

            updateContent { it.copy(loadingMessage = "구름이가 네트워크 연결을 확인하는중...", progress = 0.4f) }
            delay(400)

            if (!networkManager.checkCurrentNetwork()) {
                updateContent { it.copy(navTarget = NavTarget.NoNetwork, isLoading = false, progress = 1f) }
                return@launch
            }

            val userId = getCurrentUserIdUseCase()
            updateContent { it.copy(loadingMessage = "구름이가 사용자 정보를 확인하는중...", progress = 0.7f) }
            delay(400)

            if (userId == null) {
                updateContent {
                    it.copy(
                        loadingMessage = "로그인이 필요해요",
                        navTarget = NavTarget.Login,
                        progress = 0.99f,
                        isLoading = false,
                    )
                }
                emitNavigationIfReady()
            } else {
                val profile = getUserUseCase(userId).getOrNull()
                val hasNickname = !profile?.nickname.isNullOrBlank()
                if (hasNickname) setOnboardingCompleteUseCase(userId, true)

                val finalTarget = when {
                    !hasNickname -> NavTarget.Onboarding
                    pendingWidgetRoute != null -> NavTarget.Widget(pendingWidgetRoute!!)
                    else -> NavTarget.Home
                }

                updateContent {
                    it.copy(
                        loadingMessage = when (finalTarget) {
                            is NavTarget.Onboarding -> "처음 오셨네요! 구름한장을 소개해드릴게요"
                            is NavTarget.Widget -> "위젯에서 요청한 페이지로 이동중..."
                            else -> "구름이와 홈으로 이동중"
                        },
                        progress = 0.99f,
                    )
                }
                delay(500)

                updateContent { it.copy(progress = 1f, isLoading = false, navTarget = finalTarget) }
                emitNavigationIfReady()
            }
        }
    }

    private fun emitNavigationIfReady() {
        val state = _uiState.value.contentOrDefault()
        if (didSendNavigation || !state.permissionHandled || state.isLoading) return
        val effect = when (val target = state.navTarget) {
            NavTarget.Login -> SplashEffect.NavigateToLogin
            NavTarget.Onboarding -> SplashEffect.NavigateToOnBoarding
            NavTarget.Home -> SplashEffect.NavigateToHome
            is NavTarget.Widget -> SplashEffect.NavigateToWidget(target.route)
            NavTarget.Loading,
            NavTarget.NoNetwork -> null
        } ?: return

        didSendNavigation = true
        viewModelScope.launch { _effect.send(effect) }
    }

    private inline fun updateContent(
        crossinline transform: (SplashUiState.Content) -> SplashUiState.Content,
    ) {
        _uiState.update { current -> transform(current.contentOrDefault()) }
    }
}

sealed interface SplashEffect {
    data object NavigateToLogin : SplashEffect
    data object NavigateToOnBoarding : SplashEffect
    data object NavigateToHome : SplashEffect
    data class NavigateToWidget(val route: Any) : SplashEffect
}
