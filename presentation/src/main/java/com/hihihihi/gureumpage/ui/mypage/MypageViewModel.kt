package com.hihihihi.gureumpage.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.usecase.theme.GetDarkThemeUserCase
import com.hihihihi.domain.usecase.theme.SetDarkThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val getDarkThemeUserCase: GetDarkThemeUserCase, // 다크모드 조회용 usecase
    private val setDarkThemeUseCase: SetDarkThemeUseCase // 다크모드 저장용 Usecase
) : ViewModel() {

    //DataStore 에서 다크모드 여부를 Flow 로 받아오는 StateFlow 형태로 보관
    val isDarkTheme: StateFlow<Boolean> =
        getDarkThemeUserCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    //스위치 클릭 시 호출
    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            setDarkThemeUseCase(enabled) //선택된 모드 상태를 저장
        }
    }
}