package com.hihihihi.domain.usecase.quote

import com.hihihihi.domain.repository.QuoteRepository
import javax.inject.Inject

class DeleteQuoteUseCase @Inject constructor(
    private val repository: QuoteRepository
) {
    suspend operator fun invoke(quoteId: String): Result<Unit> = repository.deleteQuote(quoteId)
}