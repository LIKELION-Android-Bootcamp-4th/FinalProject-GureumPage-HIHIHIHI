package com.hihihihi.domain.repository

import com.hihihihi.domain.model.DomainUserBook
import kotlinx.coroutines.flow.Flow

interface UserBookRepository {
    fun getUserBooks(userId: String): Flow<List<DomainUserBook>>
}