package com.hihihihi.gureumpage.ui.quotes

import com.hihihihi.domain.model.Quote

// 홈 화면의 UI 상태를 나타내는 데이터 클래스
data class QuotesUiState(
    val isLoading: Boolean = false, // 데이터 로딩 중인지 여부
    val quotes: List<Quote> = emptyList(), // 사용자 필사 목록
    val errorMessage: String? = null, // 에러 메세지 (null이면 에러 없음)
)
