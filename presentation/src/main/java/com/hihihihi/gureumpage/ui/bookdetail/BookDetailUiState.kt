package com.hihihihi.gureumpage.ui.bookdetail

import com.hihihihi.domain.model.Book
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.UserBook

// 도서 상세 화면 UI 상태를 담는 데이터 클래스
data class BookDetailUiState(
    val userBook: UserBook? = null,                // 사용자가 등록한 책 정보 (없을 수도 있음)
    val isLoading: Boolean = false,         // 화면 전체 로딩 상태 여부
    val errorMessage: String? = null,       // 에러 메시지 (null이면 에러 없음)
    val quotes: List<Quote> = emptyList(),  // 필사 목록
    val addQuoteState: AddQuoteState = AddQuoteState()  // 필사 추가 상태 관리 객체
)

// 필사 추가 작업에 대한 상태를 별도로 관리하기 위한 클래스
data class AddQuoteState(
    val isLoading: Boolean = false,    // 필사 추가 요청 중인지 여부
    val isSuccess: Boolean = false,    // 필사 추가 성공 여부
    val error: String? = null,         // 필사 추가 중 발생한 에러 메시지
    val message: String? = null        // (필요 시) 추가적인 성공/정보 메시지
)