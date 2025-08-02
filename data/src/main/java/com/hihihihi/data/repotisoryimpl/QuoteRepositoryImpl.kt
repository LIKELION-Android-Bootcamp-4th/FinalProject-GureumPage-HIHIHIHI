package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.mapper.toDto
import com.hihihihi.data.remote.datasource.QuoteRemoteDataSource
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.repository.QuoteRepository
import javax.inject.Inject

class QuoteRepositoryImpl @Inject constructor(
    private val quoteRemoteDataSource: QuoteRemoteDataSource
): QuoteRepository {
    override suspend fun addQuote(quote: Quote): Result<Unit> {
        val quoteDto = quote.toDto()
        return quoteRemoteDataSource.addQuote(quoteDto)
    }

}