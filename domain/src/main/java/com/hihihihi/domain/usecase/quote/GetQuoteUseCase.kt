package com.hihihihi.domain.usecase.quote

import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuoteUseCase @Inject constructor(
    private val repository: QuoteRepository
) {
    operator fun invoke(userId: String): Flow<List<Quote>> {
        return repository.getQuotes(userId)
    }
}
