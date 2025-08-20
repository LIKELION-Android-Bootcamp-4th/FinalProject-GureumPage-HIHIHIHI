package com.hihihihi.domain.usecase.userbook

import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.UserBookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserBooksByStatusUseCase @Inject constructor(
    private val repository: UserBookRepository
) {
    operator fun invoke(userId: String, status: ReadingStatus): Flow<List<UserBook>> {
        return repository.getUserBooksByStatus(userId, status)
    }
}

