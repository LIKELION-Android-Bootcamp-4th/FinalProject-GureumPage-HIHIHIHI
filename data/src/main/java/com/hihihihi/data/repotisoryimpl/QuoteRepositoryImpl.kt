package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.mapper.toDomain
import com.hihihihi.data.mapper.toDto
import com.hihihihi.data.remote.datasource.QuoteRemoteDataSource
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// QuoteRepository 인터페이스 구현체
class QuoteRepositoryImpl @Inject constructor(
    private val quoteRemoteDataSource: QuoteRemoteDataSource // 원격 데이터 소스 주입 (Firestore)
): QuoteRepository {
    // 명언 추가 함수 구현
    override suspend fun addQuote(quote: Quote): Result<Unit> {
        // Domain 모델인 Quote를 DTO로 변환
        val quoteDto = quote.toDto()
        // RemoteDataSource에 DTO 전달하여 실제 데이터 저장 수행
        return quoteRemoteDataSource.addQuote(quoteDto)
    }

    override fun getQuotes(userId: String): Flow<List<Quote>> {
        return quoteRemoteDataSource.getQuotes(userId)
            .map { dtoList -> dtoList.map { it.toDomain() } }
    }

    override fun getQuotesByUserBookId(userBookId: String): Flow<List<Quote>> {
        return quoteRemoteDataSource.getQuotesByUserBookId(userBookId)
            .map { dtoList -> dtoList.map { it.toDomain() } }
    }


}
