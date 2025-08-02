package com.hihihihi.domain.repository

import com.hihihihi.domain.model.UserBook
import kotlinx.coroutines.flow.Flow

interface UserBookRepository {
    fun getUserBooks(userId: String): Flow<List<UserBook>>
}