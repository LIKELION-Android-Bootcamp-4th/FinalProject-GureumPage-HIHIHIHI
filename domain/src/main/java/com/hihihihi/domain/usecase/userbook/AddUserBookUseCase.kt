package com.hihihihi.domain.usecase.userbook

import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.UserBookRepository
import javax.inject.Inject

class AddUserBookUseCase @Inject constructor(
    private val userBookRepository: UserBookRepository
) {
    suspend operator fun invoke(userBook: UserBook): Result<Unit> {
        return try {
            userBookRepository.addUserBook(userBook)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}