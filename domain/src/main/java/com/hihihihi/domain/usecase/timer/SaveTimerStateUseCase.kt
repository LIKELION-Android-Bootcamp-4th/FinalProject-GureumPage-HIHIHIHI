package com.hihihihi.domain.usecase.timer

import com.hihihihi.domain.model.TimerState
import com.hihihihi.domain.repository.TimerRepository
import javax.inject.Inject

class SaveTimerStateUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(state: TimerState) {
        repository.saveTimerState(state)
    }
}