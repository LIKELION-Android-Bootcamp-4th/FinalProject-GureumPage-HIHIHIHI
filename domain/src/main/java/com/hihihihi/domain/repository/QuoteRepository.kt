package com.hihihihi.domain.repository

import com.hihihihi.domain.model.Quote

interface QuoteRepository {
    suspend fun addQuote(quote: Quote): Result<Unit>
}