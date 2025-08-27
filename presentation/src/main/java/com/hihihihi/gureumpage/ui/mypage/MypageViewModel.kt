package com.hihihihi.gureumpage.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.data.remote.datasourceimpl.FirestoreListenerManager
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.usecase.user.GetMyPageDataUseCase
import com.hihihihi.domain.usecase.user.GetThemeFlowUseCase
import com.hihihihi.domain.usecase.user.SetThemeUseCase
import com.hihihihi.domain.usecase.user.UpdateNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val setThemeUseCase: SetThemeUseCase,                   // 다크모드 저장용 Usecase
    private val getMyPageDataUseCase: GetMyPageDataUseCase,
//    private val getDailyReadPagesUseCase: GetDailyReadPagesUseCase, // 잔디
//    private val getUserUseCase: GetUserUseCase,                     // 사용자 정보 조회
    private val updateNicknameUseCase: UpdateNicknameUseCase,       // 닉네임 변경
//    private val getUserBooksUseCase: GetUserBooksUseCase,           //총 권수 계산용
    getTheme: GetThemeFlowUseCase,
) : ViewModel() {

    // FirebaseAuth로 현재 uid 참조
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUid: String?
        get() = auth.currentUser?.uid

    //DataStore 에서 다크모드 여부를 Flow 로 받아오는 StateFlow 형태로 보관
    val theme = getTheme().stateIn(viewModelScope, SharingStarted.Lazily, GureumThemeType.DARK)

    //스위치 클릭 시 호출
    fun toggleTheme(theme: GureumThemeType) {
        viewModelScope.launch {
            setThemeUseCase(theme) //선택된 모드 상태를 저장
        }
    }

    // 마이페이지 사용자 정보
    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState

    //로그아웃 이벤트
    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            getMyPageDataUseCase(currentUid ?: return@launch)
                .catch { e ->
                    _uiState.update {
                        it.copy(errorMessage = e.message, isLoading = false)
                    }
                }
                .collect { myPageData ->
                    _uiState.update {
                        it.copy(myPageData = myPageData, isLoading = false)
                    }

                }
        }
    }

    //닉네임 변경, 현재 uid 기준으로
    fun changeNickname(newNickname: String) = viewModelScope.launch {
        val uid = currentUid ?: return@launch
        runCatching { updateNicknameUseCase(uid, newNickname) }
            .onSuccess {}
            .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
    }


    //로그아웃: firebase 세션 종료 후 이벤트 발생
    fun logout() = viewModelScope.launch {
        runCatching {
            FirestoreListenerManager.clearAll()

            auth.signOut()
        }
            .onSuccess {
                _uiState.value = MyPageUiState(isLoading = false)
                _logoutEvent.tryEmit(Unit)
            }
            .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
    }
}