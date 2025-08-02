package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.UserBookDto
import kotlinx.coroutines.flow.Flow

interface UserBookRemoteDataSource {
    fun getUserBooks(userId: String): Flow<List<UserBookDto>>
}
