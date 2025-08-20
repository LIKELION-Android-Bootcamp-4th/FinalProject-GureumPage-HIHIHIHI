package com.hihihihi.domain.usecase.quote

import com.hihihihi.domain.repository.QuoteRepository
import javax.inject.Inject

class UpdateQuoteUseCase @Inject constructor(
    private val repository: QuoteRepository
) {
    suspend operator fun invoke(
        quoteId: String,
        content: String,
        pageNumber: Int?
    ): Result<Unit> = repository.updateQuote(quoteId, content, pageNumber)
}