package com.hihihihi.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.user.SetNicknameUseCase
import com.hihihihi.domain.usecase.user.SetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.SetThemeUseCase
import com.hihihihi.presentation.ui.onboarding.model.OnboardingStep
import com.hihihihi.presentation.utils.NicknameValidator.validateNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val setOnboardingCompleteUseCase: SetOnboardingCompleteUseCase,
    private val setNicknameUseCase: SetNicknameUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnBoardingUiState())
    val uiState: StateFlow<OnBoardingUiState> = _uiState.asStateFlow()

    val steps: List<OnboardingStep> = listOf(
        OnboardingStep.Welcome,
        OnboardingStep.Nickname,
        OnboardingStep.Purpose,
        OnboardingStep.Feature,
        OnboardingStep.Theme,
        OnboardingStep.Finish,
    )

    fun updateNickname(nickname: String) {
        _uiState.update { it.copy(nickname = nickname) }
    }

    fun saveNickname() {
        val userId: String? = getCurrentUserIdUseCase()
        viewModelScope.launch { setNicknameUseCase(userId!!, _uiState.value.nickname.trim()) }
    }

    fun togglePurpose(purpose: String) {
        _uiState.update { state ->
            val current = state.selectedPurposes.toMutableList()
            if (current.contains(purpose)) current.remove(purpose) else current.add(purpose)
            state.copy(selectedPurposes = current)
        }
    }

    fun featurePageChanged(page: Int, count: Int) {
        _uiState.update { it.copy(currentInnerPage = page, featurePageCount = count - 1) }
    }

    fun isNextEnabled(step: OnboardingStep): Boolean {
        val state = _uiState.value
        return when (step) {
            OnboardingStep.Nickname -> state.nickname.validateNickname()
            OnboardingStep.Purpose -> state.selectedPurposes.isNotEmpty()
            OnboardingStep.Feature -> state.currentInnerPage >= state.featurePageCount
            OnboardingStep.Theme -> state.theme != null
            else -> true
        }
    }

    fun selectTheme(theme: GureumThemeType) {
        _uiState.update { it.copy(theme = theme) }
    }

    fun saveOnboardingComplete() {
        viewModelScope.launch {
            val uid = getCurrentUserIdUseCase() ?: return@launch
            _uiState.value.theme?.let { setThemeUseCase(it) }
            saveNickname()
            setOnboardingCompleteUseCase(uid, true)
        }
    }
}
