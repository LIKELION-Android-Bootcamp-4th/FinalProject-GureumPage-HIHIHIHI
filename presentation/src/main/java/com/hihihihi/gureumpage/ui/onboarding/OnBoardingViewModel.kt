package com.hihihihi.gureumpage.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hihihihi.gureumpage.ui.onboarding.model.OnboardingStep
import com.hihihihi.gureumpage.ui.onboarding.model.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor(
//    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _steps = MutableStateFlow<List<OnboardingStep>>(emptyList())
    val steps: StateFlow<List<OnboardingStep>> = _steps

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

    var nickname by mutableStateOf("")
        private set

    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    val selectedPurposes = mutableStateListOf<String>()
    fun togglePurpose(purpose: String) {
        if (selectedPurposes.contains(purpose)) selectedPurposes.remove(purpose)
        else selectedPurposes.add(purpose)
    }

    fun isNextEnabled(step: OnboardingStep): Boolean {
        return when (step) {
            OnboardingStep.Nickname -> nickname.length in 2..8
            OnboardingStep.Purpose -> selectedPurposes.isNotEmpty()
            OnboardingStep.Theme -> selectedTheme != null
            else -> true
        }
    }

    var selectedTheme by mutableStateOf<ThemeType?>(null)
        private set

    fun selectTheme(theme: ThemeType) {
        selectedTheme = theme
    }

    // TODO 데이터 스토어에 테마 저장
//    fun saveTheme() {
//        selectedTheme?.let { theme ->
//            viewModelScope.launch {
//                dataStoreManager.saveTheme(theme)
//            }
//        }
//    }
}