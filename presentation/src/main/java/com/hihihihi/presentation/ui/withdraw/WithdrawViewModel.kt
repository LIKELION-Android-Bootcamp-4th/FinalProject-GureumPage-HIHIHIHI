package com.hihihihi.presentation.ui.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.auth.WithdrawUserUseCase
import com.hihihihi.domain.usecase.user.ClearUserDataUseCase
import com.hihihihi.domain.usecase.user.GetUserUseCase
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val clearUserDataUseCase: ClearUserDataUseCase,
    private val withdrawUserUseCase: WithdrawUserUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WithdrawUiState())
    val uiState: StateFlow<WithdrawUiState> = _uiState.asStateFlow()

    private val _effect = Channel<WithdrawEffect>(Channel.BUFFERED)
    val effect: Flow<WithdrawEffect> = _effect.receiveAsFlow()

    private fun setLoading(isLoading: Boolean, message: String = "") {
        _uiState.value = _uiState.value.copy(
            isLoading = isLoading,
            loadingMessage = message,
            errorMessage = null
        )
    }

    private fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun withdrawUser() = viewModelScope.launch {
        setLoading(true, "사용자 정보를 확인하는중...")

        try {
            val currentUserUid = getCurrentUserIdUseCase()
            if (currentUserUid == null) {
                setError("로그인된 사용자가 없습니다")
                return@launch
            }

            val user = getUserUseCase(currentUserUid).getOrNull()
            val providerId = user?.provider

            if (providerId != null) {
                setLoading(true, "소셜 계정 연결 해제 및 사용자 데이터 삭제하는중...")
            } else {
                setLoading(true, "사용자 데이터를 삭제하는중...")
            }

            withdrawUserUseCase(providerId ?: "").getOrThrow()

            clearUserDataUseCase.clearAll()

            setLoading(false)
            _effect.send(WithdrawEffect.NavigateToLogin)
        } catch (e: Exception) {
            Log.e("WithdrawViewModel", "계정 탈퇴 실패", e)
            val errorMessage = when {
                e.message?.contains("unauthenticated") == true -> "인증이 필요합니다"
                e.message?.contains("not-found") == true -> "사용자를 찾을 수 없습니다"
                e.message?.contains("permission-denied") == true -> "권한이 없습니다"
                else -> "탈퇴 처리 중 오류가 발생했습니다"
            }
            setError(errorMessage)
        }
    }
}

sealed interface WithdrawEffect {
    data object NavigateToLogin : WithdrawEffect
}
