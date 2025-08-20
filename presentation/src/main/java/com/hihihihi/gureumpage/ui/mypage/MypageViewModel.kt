package com.hihihihi.gureumpage.ui.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.usecase.daily.GetDailyReadPagesUseCase
import com.hihihihi.domain.usecase.user.GetThemeFlowUseCase
import com.hihihihi.domain.usecase.user.GetUserUseCase
import com.hihihihi.domain.usecase.user.SetThemeUseCase
import com.hihihihi.domain.usecase.user.UpdateNicknameUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val setThemeUseCase: SetThemeUseCase,                   // 다크모드 저장용 Usecase
    private val getDailyReadPagesUseCase: GetDailyReadPagesUseCase, // 잔디
    private val getUserUseCase: GetUserUseCase,                     // 사용자 정보 조회
    private val updateNicknameUseCase: UpdateNicknameUseCase,       // 닉네임 변경
    private val getUserBooksUseCase: GetUserBooksUseCase,           //총 권수 계산용
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

    //잔디 통계
    private val _readingStats = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val readingStats: StateFlow<Map<LocalDate, Int>> = _readingStats

    // 마이페이지 사용자 정보
    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState

    //로그아웃 이벤트
    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    init {
        // 로그인 상태에서만 진입 -> uid 없으면 로딩 해제
        val uid = currentUid
        if (uid != null) {
            loadUser(uid) //프로필
            loadReadingStats(uid) //통계
            loadUserBookStats(uid) // 총 권수
        } else {
            // 혹시 모를 예외 흐름에서도 화면은 멈추지 않게 로딩만 해제
            _uiState.update { it.copy(loading = false) }
        }
    }

    fun reloadAll() {
        currentUid?.let {
            loadUser(it)
            loadReadingStats(it)
            loadUserBookStats(it)
        }
    }

    private fun loadUser(userId: String) = viewModelScope.launch {
        _uiState.update { it.copy(loading = true, error = null) }
        runCatching { getUserUseCase(userId) }
            .onSuccess { user ->
                _uiState.update {
                    if (user == null) MyPageUiState(loading = false)
                    else it.copy(
                        loading = false,
                        nickname = user.nickname,
                        appellation = user.appellation,
                        error = null
                    )
                }
            }
            .onFailure { e ->
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
    }

    //닉네임 변경, 현재 uid 기준으로
    fun changeNickname(newNickname: String) = viewModelScope.launch {
        val uid = currentUid ?: return@launch
        runCatching { updateNicknameUseCase(uid, newNickname) }
            .onSuccess { _uiState.update { it.copy(nickname = newNickname) } }
            .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
    }

    // 잔디
    private fun loadReadingStats(userId: String) = viewModelScope.launch {
        runCatching { getDailyReadPagesUseCase(userId) }
            .onSuccess { dailies ->
                val mapped = dailies.associate { it.date to it.totalReadPageCount }
                _readingStats.value = mapped
            }
            .onFailure { e ->
                _readingStats.value = emptyMap()
                _uiState.update { it.copy(error = e.message) }
            }
    }

    // 독서 통계
    private fun loadUserBookStats(userId: String) = viewModelScope.launch {
        runCatching {
            val books = getUserBooksUseCase(userId).first()

            // 완료 도서(권수/페이지 집계 기준)
            val finished = books.filter { it.status.name.equals("FINISHED", true) || it.endDate != null }
            val finishedCount = finished.size
            val totalPages = finished.sumOf { it.totalPage }

            //  총 독서 시간 초' → 분으로 변환해서 합산
            val totalReadMinutes = books.sumOf { (it.totalReadTime / 60).coerceAtLeast(0) }

            Triple(finishedCount, totalPages, totalReadMinutes)
        }
            .onSuccess { (finishedCount, totalPages, totalReadMinutes) ->
                _uiState.update {
                    it.copy(
                        totalBooks = finishedCount,
                        totalPages = totalPages,
                        totalReadMinutes = totalReadMinutes
                    )
                }
            }
            .onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
    }

    //로그아웃: firebase 세션 종료 후 이벤트 발생
    fun logout() = viewModelScope.launch {
        runCatching { auth.signOut() }
            .onSuccess {
                _uiState.value = MyPageUiState(loading = false)
                _logoutEvent.tryEmit(Unit)
            }
            .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
    }
}