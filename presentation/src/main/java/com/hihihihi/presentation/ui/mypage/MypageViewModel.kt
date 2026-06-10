package com.hihihihi.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.auth.LogoutUseCase
import com.hihihihi.domain.usecase.user.GetMyPageDataUseCase
import com.hihihihi.domain.usecase.user.GetThemeFlowUseCase
import com.hihihihi.domain.usecase.user.SetThemeUseCase
import com.hihihihi.domain.usecase.user.UpdateNicknameUseCase
import com.hihihihi.presentation.ui.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val setThemeUseCase: SetThemeUseCase,
    private val getMyPageDataUseCase: GetMyPageDataUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase,
    getTheme: GetThemeFlowUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val currentUid: String?
        get() = getCurrentUserIdUseCase()

    val theme = getTheme().stateIn(viewModelScope, SharingStarted.Lazily, GureumThemeType.DARK)

    fun toggleTheme(theme: GureumThemeType) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }

    private val _uiState = MutableStateFlow<MyPageUiState>(MyPageUiState.Loading)
    val uiState: StateFlow<MyPageUiState> = _uiState

    private val _effect = Channel<MypageEffect>(Channel.BUFFERED)
    val effect: Flow<MypageEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                getMyPageDataUseCase(currentUid ?: return@launch)
                    .catch { e ->
                        _uiState.update { current ->
                            MyPageUiState.Error(
                                message = e.message ?: "사용자 정보를 불러오는데 실패했어요",
                                previous = current as? MyPageUiState.Content,
                            )
                        }
                    }
                    .collect { myPageData ->
                        updateContent { it.copy(myPageUiModel = myPageData.toUiModel()) }
                    }
            } catch (e: Exception) {
                _uiState.update { current ->
                    MyPageUiState.Error(
                        message = e.message ?: "사용자 정보를 불러오는데 실패했어요",
                        previous = current as? MyPageUiState.Content,
                    )
                }
            }
        }
    }

    fun changeNickname(newNickname: String) = viewModelScope.launch {
        val uid = currentUid ?: return@launch
        runCatching { updateNicknameUseCase(uid, newNickname) }
            .onSuccess {}
            .onFailure { e -> _effect.send(MypageEffect.ShowMessage(e.message ?: "닉네임 변경에 실패했습니다.")) }
    }

    fun logout() = viewModelScope.launch {
        runCatching {
            logoutUseCase()
            }
            .onSuccess {
                _uiState.value = MyPageUiState.Content(myPageUiModel = null)
                _effect.send(MypageEffect.NavigateToLogin)
            }
            .onFailure { e -> _effect.send(MypageEffect.ShowMessage(e.message ?: "로그아웃에 실패했습니다.")) }
    }

    fun onLogoutClick() {
        updateContent { it.copy(dialogState = MyPageDialogState.Logout) }
    }

    fun onNicknameChangeClick() {
        updateContent { it.copy(dialogState = MyPageDialogState.NicknameChange) }
    }

    fun dismissDialog() {
        updateContent { it.copy(dialogState = MyPageDialogState.None) }
    }

    fun onWithdrawClick() {
        val userName = _uiState.value.contentOrDefault().myPageUiModel?.nickname
        if (userName.isNullOrBlank()) {
            viewModelScope.launch {
                _effect.send(MypageEffect.ShowMessage("사용자 정보를 불러오는 중입니다. 잠시 후 다시 시도해 주세요."))
            }
            return
        }
        viewModelScope.launch { _effect.send(MypageEffect.NavigateToWithdraw(userName)) }
    }

    private inline fun updateContent(
        crossinline transform: (MyPageUiState.Content) -> MyPageUiState.Content,
    ) {
        _uiState.update { current -> transform(current.contentOrDefault()) }
    }
}

sealed interface MyPageDialogState {
    data object None : MyPageDialogState
    data object Logout : MyPageDialogState
    data object NicknameChange : MyPageDialogState
}

sealed interface MypageEffect {
    data object NavigateToLogin : MypageEffect
    data class NavigateToWithdraw(val userName: String) : MypageEffect
    data class ShowMessage(val message: String) : MypageEffect
}
