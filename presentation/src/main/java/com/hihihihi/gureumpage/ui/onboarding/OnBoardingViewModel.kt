package com.hihihihi.gureumpage.ui.onboarding

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.usecase.user.SetNicknameUseCase
import com.hihihihi.domain.usecase.user.SetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.SetThemeUseCase
import com.hihihihi.gureumpage.common.utils.validateNickname
import com.hihihihi.gureumpage.ui.onboarding.model.OnboardingStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val setOnboardingCompleteUseCase: SetOnboardingCompleteUseCase,
    private val setNicknameUseCase: SetNicknameUseCase,
    private val setThemeUseCase: SetThemeUseCase
) : ViewModel() {
    private val _steps = MutableStateFlow<List<OnboardingStep>>(emptyList())
    val steps: StateFlow<List<OnboardingStep>> = _steps

    val selectedPurposes = mutableStateListOf<String>()
    var nickname by mutableStateOf("")
        private set
    var selectedTheme by mutableStateOf<GureumThemeType?>(null)
        private set

    init {
        _steps.value = listOf(
            OnboardingStep.Welcome,
            OnboardingStep.Nickname,
            OnboardingStep.Purpose,
            OnboardingStep.Feature,
            OnboardingStep.Theme,
            OnboardingStep.Finish
        )
    }

    fun updateNickname(nickname: String) {
        this.nickname = nickname
        Log.d("Onboarding", "저장된 닉네임 ${this.nickname}")
    }

    fun saveNickname() {
        viewModelScope.launch {
            setNicknameUseCase(nickname)
        }
    }

    // TODO: 추후 사용자 앱 설치 목적 파악을 위해 DB 테이블 만들기 고려
    fun togglePurpose(purpose: String) {
        if (selectedPurposes.contains(purpose)) selectedPurposes.remove(purpose)
        else selectedPurposes.add(purpose)
    }

    fun isNextEnabled(step: OnboardingStep): Boolean {
        return when (step) {
            OnboardingStep.Nickname -> nickname.validateNickname()
            OnboardingStep.Purpose -> selectedPurposes.isNotEmpty()
            OnboardingStep.Theme -> selectedTheme != null
            else -> true
        }
    }

    fun saveOnboardingComplete() {
        viewModelScope.launch {
            setOnboardingCompleteUseCase(true)
        }
    }

    fun saveTheme(theme: GureumThemeType) {
        selectedTheme = theme
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }
}