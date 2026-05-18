package com.hihihihi.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.notification.GetNotificationSettingsUseCase
import com.hihihihi.domain.usecase.user.GetHomeDataUseCase
import com.hihihihi.domain.usecase.user.UpdateDailyGoalTimeUseCase
import com.hihihihi.presentation.ui.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val changeDailyGoalTimeUseCase: UpdateDailyGoalTimeUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
) : ViewModel() {

    private val currentUid: String?
        get() = getCurrentUserIdUseCase()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            currentUid?.let { uid ->
                getHomeDataUseCase(uid)
                    .catch { e ->
                        _uiState.update {
                            it.copy(errorMessage = e.message, isLoading = false)
                        }
                    }
                    .collect { homeData ->
                        _uiState.update {
                            it.copy(homeUiModel = homeData.toUiModel(), isLoading = false)
                        }
                    }
            }
        }

        viewModelScope.launch {
            getNotificationSettingsUseCase()
                .catch { exception ->
                    Log.e(TAG, "알림 설정 조회 실패", exception)
                }
                .collect { settings ->
                    _uiState.update { it.copy(notificationSettings = settings) }
                }
        }
    }

    fun changeDailyGoalTime(dailyGoalTime: Int) {
        viewModelScope.launch {
            currentUid?.let { uid ->
                changeDailyGoalTimeUseCase(uid, dailyGoalTime)
            }
        }
    }

    private companion object {
        const val TAG = "HomeViewModel"
    }
}
