package com.hihihihi.domain.usecase.quote

import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.repository.QuoteRepository
import javax.inject.Inject

// 명언 추가 기능을 수행하는 UseCase
class AddQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository // 명언 저장소 주입
) {
    // invoke 연산자 함수로, UseCase를 함수처럼 호출 가능하게 만듦
    suspend operator fun invoke(quote: Quote): Result<Unit> {
        return try {
            // 저장소에 명언 추가 시도
            quoteRepository.addQuote(quote)
        } catch (e: Exception) {
            // 예외 발생 시 실패 결과 반환
            Result.failure(e)
        }
    }
}