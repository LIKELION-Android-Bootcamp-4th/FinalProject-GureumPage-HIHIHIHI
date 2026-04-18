package com.hihihihi.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.auth.SignInWithSocialTokenUseCase
import com.hihihihi.domain.usecase.auth.SocialProvider
import com.hihihihi.domain.usecase.user.GetLastProviderUseCase
import com.hihihihi.domain.usecase.user.GetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.GetUserUseCase
import com.hihihihi.domain.usecase.user.SetLastProviderUseCase
import com.hihihihi.domain.usecase.user.SetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.WaitForUserDocumentCreationUseCase
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithSocialTokenUseCase: SignInWithSocialTokenUseCase,
    private val getOnboardingCompleteUseCase: GetOnboardingCompleteUseCase,
    private val setOnboardingCompleteUseCase: SetOnboardingCompleteUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val setLastProviderUseCase: SetLastProviderUseCase,
    private val getLastProviderUseCase: GetLastProviderUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val waitForUserDocumentCreationUseCase: WaitForUserDocumentCreationUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect: Flow<LoginEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                lastProvider = getLastProvider(),
                errorMessage = null
            )
        }
    }

    private suspend fun getLastProvider(): String {
        return getLastProviderUseCase().first()
    }

    private suspend fun navigateAfterLogin() {
        setLoading(true, "사용자 정보를 설정하는 중...")

        val currentUserUid = getCurrentUserIdUseCase()
        if (currentUserUid == null) {
            setError("로그인 정보를 찾을 수 없습니다")
            return
        }

        try {
            waitForUserDocumentCreationUseCase(currentUserUid).getOrThrow()
        } catch (_: Exception) {
            setError("사용자 정보 설정에 실패했습니다. 다시 시도해주세요.")
            return
        }

        setLoading(true, "사용자 정보를 확인하는 중...")

        val profile = getUserUseCase(currentUserUid).getOrNull()
        val hasNickname = !profile?.nickname.isNullOrBlank()
        if (hasNickname) setOnboardingCompleteUseCase(currentUserUid, true)

        val isOnboardingComplete = getOnboardingCompleteUseCase(currentUserUid).firstOrNull() ?: false

        setLoading(false)
        if (isOnboardingComplete && hasNickname) {
            _effect.send(LoginEffect.NavigateToHome)
        } else {
            _effect.send(LoginEffect.NavigateToOnBoarding)
        }
    }


    private fun setLoading(isLoading: Boolean, message: String = "") {
        _uiState.value = _uiState.value.copy(
            isLoading = isLoading,
            loadingMessage = message,
            errorMessage = null
        )
    }

    internal fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun loginWithSocialToken(provider: SocialProvider, accessToken: String) {
        viewModelScope.launch {
            try {
                setLoading(true, "로그인 중...")
                signInWithSocialTokenUseCase(provider, accessToken)
                setLastProviderUseCase(provider.name.lowercase())
                navigateAfterLogin()
            } catch (e: Exception) {
                Log.e("LoginViewModel", "소셜 로그인 실패", e)
                setError("로그인에 실패했습니다. 다시 시도해주세요.")
            }
        }
    }
}

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
    data object NavigateToOnBoarding : LoginEffect
}
