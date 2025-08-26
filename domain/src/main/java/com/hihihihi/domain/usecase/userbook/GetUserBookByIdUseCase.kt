package com.hihihihi.domain.usecase.userbook

import com.hihihihi.domain.repository.UserBookRepository
import javax.inject.Inject

class GetUserBookByIdUseCase @Inject constructor(
    private val repository: UserBookRepository
) {
    operator fun invoke(userBookId: String) = repository.getUserBook(userBookId)
}