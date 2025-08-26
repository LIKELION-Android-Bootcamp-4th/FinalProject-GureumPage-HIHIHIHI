package com.hihihihi.domain.usecase.daily

import com.hihihihi.domain.model.DailyReadPage
import com.hihihihi.domain.repository.DailyReadPageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyReadPagesUseCase @Inject constructor(
    private val repository: DailyReadPageRepository
) {
    operator fun invoke(uid: String): Flow<List<DailyReadPage>> =
        repository.getDailyReadPages(uid)
}