package com.hihihihi.domain.usecase.quote

import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuoteByUserBookIdUseCase @Inject constructor(
    private val repository: QuoteRepository
) {
    operator fun invoke(userBookId: String): Flow<List<Quote>> =
        repository.getQuotesByUserBookId(userBookId)
}