package com.hihihihi.domain.usecase.timer

import com.hihihihi.domain.model.TimerState
import com.hihihihi.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTimerStateUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    operator fun invoke(): Flow<TimerState> = repository.observeTimerState()
}