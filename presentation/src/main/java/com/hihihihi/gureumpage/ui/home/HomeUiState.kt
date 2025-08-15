package com.hihihihi.gureumpage.ui.home

import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.usecase.user.HomeData

// 홈 화면의 UI 상태를 나타내는 데이터 클래스
data class HomeUiState(
    val isLoading: Boolean = false, // 데이터 로딩 중인지
    val homeData : HomeData? = null, // 홈 화면에 표시할 데이터
    val errorMessage: String? = null, // 에러 메세지 (null이면 에러 없음)
)
