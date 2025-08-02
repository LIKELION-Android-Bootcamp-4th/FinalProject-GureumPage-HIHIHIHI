package com.hihihihi.domain.usecase.quote

import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.repository.QuoteRepository
import javax.inject.Inject

class AddQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(quote: Quote): Result<Unit> {
        return try {
          quoteRepository.addQuote(quote)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}