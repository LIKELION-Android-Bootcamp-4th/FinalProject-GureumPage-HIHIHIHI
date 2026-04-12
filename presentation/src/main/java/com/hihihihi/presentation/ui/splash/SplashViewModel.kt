package com.hihihihi.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.repository.NetworkMonitor
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.user.GetUserUseCase
import com.hihihihi.domain.usecase.user.SetOnboardingCompleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val setOnboardingCompleteUseCase: SetOnboardingCompleteUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
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

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState

    private var pendingWidgetRoute: Any? = null

    fun setPendingWidgetRoute(route: Any?) {
        pendingWidgetRoute = route
    }

    fun markPermissionAsked() {
        _uiState.update { it.copy(permissionAsked = true) }
    }

    fun onPermissionResult() {
        _uiState.update { it.copy(permissionHandled = true) }
    }

    fun markSchedulersSetUp() {
        _uiState.update { it.copy(schedulersSetUp = true) }
    }

    fun checkNetworkAndProceed() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingMessage = "구름한장을 시작하는 중...", progress = 0.2f) }
            delay(400)

            _uiState.update { it.copy(loadingMessage = "구름이가 네트워크 연결을 확인하는중...", progress = 0.4f) }
            delay(400)

            if (!networkManager.checkCurrentNetwork()) {
                _uiState.update { it.copy(navTarget = NavTarget.NoNetwork, isLoading = false, progress = 1f) }
                return@launch
            }

            val userId = getCurrentUserIdUseCase()
            _uiState.update { it.copy(loadingMessage = "구름이가 사용자 정보를 확인하는중...", progress = 0.7f) }
            delay(400)

            if (userId == null) {
                _uiState.update {
                    it.copy(
                        loadingMessage = "로그인이 필요해요",
                        navTarget = NavTarget.Login,
                        progress = 0.99f,
                        isLoading = false,
                    )
                }
            } else {
                val profile = getUserUseCase(userId).getOrNull()
                val hasNickname = !profile?.nickname.isNullOrBlank()
                if (hasNickname) setOnboardingCompleteUseCase(userId, true)

                val finalTarget = when {
                    !hasNickname -> NavTarget.Onboarding
                    pendingWidgetRoute != null -> NavTarget.Widget(pendingWidgetRoute!!)
                    else -> NavTarget.Home
                }

                _uiState.update {
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

                _uiState.update { it.copy(progress = 1f, isLoading = false, navTarget = finalTarget) }
            }
        }
    }
}
