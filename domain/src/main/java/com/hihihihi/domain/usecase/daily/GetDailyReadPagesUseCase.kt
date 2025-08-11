package com.hihihihi.domain.usecase.daily

import com.hihihihi.domain.model.DailyReadPage
import com.hihihihi.domain.repository.DailyReadPageRepository
import javax.inject.Inject

class GetDailyReadPagesUseCase @Inject constructor(
    private val repository: DailyReadPageRepository
) {
    suspend operator fun invoke(uid: String): List<DailyReadPage> =
        repository.getDailyReadPages(uid)
}