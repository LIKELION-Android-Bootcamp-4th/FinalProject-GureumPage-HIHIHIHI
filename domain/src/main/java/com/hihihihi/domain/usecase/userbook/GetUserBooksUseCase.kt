package com.hihihihi.domain.usecase.userbook

import com.hihihihi.domain.model.DomainUserBook
import com.hihihihi.domain.repository.UserBookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserBooksUseCase @Inject constructor(
    private val repository: UserBookRepository
){
    operator fun invoke(userId: String): Flow<List<DomainUserBook>>{
        return repository.getUserBooks(userId)
    }
}