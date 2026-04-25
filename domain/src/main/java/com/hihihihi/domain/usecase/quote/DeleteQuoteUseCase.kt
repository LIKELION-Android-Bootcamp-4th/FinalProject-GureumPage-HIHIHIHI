package com.hihihihi.domain.usecase.quote

import com.hihihihi.domain.repository.QuoteRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class DeleteQuoteUseCase @Inject constructor(
    private val repository: QuoteRepository
) {
    suspend operator fun invoke(quoteId: String): Result<Unit> = runSuspendCatching {
        repository.deleteQuote(quoteId)
    }
}
