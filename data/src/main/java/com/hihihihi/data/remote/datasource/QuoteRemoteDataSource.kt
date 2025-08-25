package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.QuoteDto
import kotlinx.coroutines.flow.Flow

// 필사(Quote) 데이터를 원격 데이터 소스(Firestore 등)와 통신하기 위한 인터페이스
interface QuoteRemoteDataSource {
    // 필사 추가 요청을 처리하는 suspend 함수
    // 성공 시 Result.success(Unit), 실패 시 Result.failure(Exception) 반환
    suspend fun addQuote(quoteDto: QuoteDto): Result<Unit>

    fun getQuotes(userId: String): Flow<List<QuoteDto>>

    fun getQuotesByUserBookId(userBookId: String): Flow<List<QuoteDto>>

    suspend fun deleteQuote(quoteId: String): Result<Unit>

    suspend fun updateQuote(
        quoteId: String,
        content: String,
        pageNumber: Int?
    ): Result<Unit>
}
