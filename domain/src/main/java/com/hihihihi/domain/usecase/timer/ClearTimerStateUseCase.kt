package com.hihihihi.domain.usecase.timer

import com.hihihihi.domain.repository.TimerRepository
import javax.inject.Inject

class ClearTimerStateUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke() {
        repository.clearTimerState()
    }
}