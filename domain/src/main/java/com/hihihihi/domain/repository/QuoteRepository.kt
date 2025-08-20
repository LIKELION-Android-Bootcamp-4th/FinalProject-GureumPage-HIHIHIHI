package com.hihihihi.domain.repository

import com.hihihihi.domain.model.Quote
import kotlinx.coroutines.flow.Flow

// 필사(Quote) 관련 데이터를 처리하는 저장소 인터페이스
interface QuoteRepository {
    // 새로운 명언을 추가하는 suspend 함수
    // 성공 시 Result.Success(Unit), 실패 시 Result.Failure(Exception) 반환
    suspend fun addQuote(quote: Quote): Result<Unit>
    fun getQuotes(userId: String): Flow<List<Quote>>
    fun getQuotesByUserBookId(userBookId: String): Flow<List<Quote>>
}
