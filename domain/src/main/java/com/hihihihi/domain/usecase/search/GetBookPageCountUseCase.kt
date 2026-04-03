package com.hihihihi.domain.usecase.search

import com.hihihihi.domain.repository.SearchRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class GetBookPageCountUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(isbn: String): Result<Int?> = runSuspendCatching {
        searchRepository.getBookPageCount(isbn)
    }
}
