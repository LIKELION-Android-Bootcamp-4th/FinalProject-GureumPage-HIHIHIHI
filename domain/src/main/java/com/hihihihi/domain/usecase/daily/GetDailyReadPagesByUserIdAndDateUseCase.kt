package com.hihihihi.domain.usecase.daily

import com.hihihihi.domain.model.DailyReadPage
import com.hihihihi.domain.repository.DailyReadPageRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class GetDailyReadPagesByUserIdAndDateUseCase @Inject constructor(
    private val repository: DailyReadPageRepository
){
    suspend operator fun invoke(userId:String, dayOfStart: Date): Flow<List<DailyReadPage>> = repository.getDailyReadPagesByUserIdAndDate(userId,dayOfStart)
}