package com.hihihihi.domain.usecase.userbook

import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.UserBookRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class PatchUserBookUseCase @Inject constructor(
    private val userBookRepository: UserBookRepository
) {
    suspend operator fun invoke(userBook: UserBook): Result<Unit> = runSuspendCatching {
        userBookRepository.patchUserBook(userBook)
    }
}