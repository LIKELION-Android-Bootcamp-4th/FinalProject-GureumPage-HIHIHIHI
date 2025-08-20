package com.hihihihi.gureumpage.ui.library

import com.hihihihi.domain.model.UserBook

//서재 화면 ui 상태 모델
data class LibraryUiState(
    val isLoading: Boolean = false, // 데이터 로딩 중인지
    val books: List<UserBook> = emptyList(), // 유저의 책 목록
    val errorMessage: String? = null // 에러 발생 시 메시지 (null이면 에러 없음)
)