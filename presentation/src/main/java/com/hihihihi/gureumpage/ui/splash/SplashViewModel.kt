package com.hihihihi.gureumpage.ui.splash

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.usecase.user.GetOnboardingCompleteUseCase
import com.hihihihi.gureumpage.common.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getOnboardingCompleteUseCase: GetOnboardingCompleteUseCase,
    private val networkManager: NetworkManager
) : ViewModel() {

    sealed interface NavTarget {
        data object Loading : NavTarget
        data object Login : NavTarget
        data object Onboarding : NavTarget
        data object Home : NavTarget
        data object NoNetwork : NavTarget
    }

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState

    private val auth = FirebaseAuth.getInstance()

    fun checkNetworkAndProceed() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loadingMessage = "구름한장을 시작하는 중...",
                progress = 0.2f
            )
            delay(400)

            _uiState.value = _uiState.value.copy(
                loadingMessage = "구름이가 네트워크 연결을 확인하는중...",
                progress = 0.4f
            )
            delay(400)

            if (!networkManager.checkCurrentNetwork()) {
                _uiState.value = _uiState.value.copy(
                    navTarget = NavTarget.NoNetwork,
                    isLoading = false,
                    progress = 1f
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                loadingMessage = "구름이가 사용자 정보를 확인하는중...",
                progress = 0.7f
            )
            delay(400)

            val user = auth.currentUser
            if (user == null) {
                _uiState.value = _uiState.value.copy(
                    loadingMessage = "로그인이 필요해요",
                    navTarget = NavTarget.Login,
                    progress = 0.99f,
                    isLoading = false
                )
            } else {
                val done = getOnboardingCompleteUseCase(user.uid).firstOrNull() ?: false
                _uiState.value = _uiState.value.copy(
                    loadingMessage = if (done) "구름이와 홈으로 이동중" else "처음 오셨네요! 구름한장을 소개해드릴게요",
                    progress = 0.99f,
                )
                delay(500)
                _uiState.value = _uiState.value.copy(
                    progress = 1f,
                    isLoading = false,
                    navTarget = if (done) NavTarget.Home else NavTarget.Onboarding
                )
            }
        }

    }
}
