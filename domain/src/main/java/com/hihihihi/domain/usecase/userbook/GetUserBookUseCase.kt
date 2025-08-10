package com.hihihihi.domain.usecase.userbook

import com.hihihihi.domain.repository.UserBookRepository
import javax.inject.Inject

class GetUserBookUseCase @Inject constructor(
    private val repository: UserBookRepository
){
    operator fun invoke(userId: String) = repository.getUserBook(userId)
}