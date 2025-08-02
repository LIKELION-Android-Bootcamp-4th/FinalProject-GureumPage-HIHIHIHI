package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.QuoteDto

interface QuoteRemoteDataSource {
    suspend fun addQuote(quoteDto: QuoteDto): Result<Unit>
}