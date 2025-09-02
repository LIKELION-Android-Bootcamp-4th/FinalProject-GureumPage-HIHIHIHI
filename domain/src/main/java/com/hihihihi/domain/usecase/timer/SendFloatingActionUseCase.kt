package com.hihihihi.domain.usecase.timer

import com.hihihihi.domain.model.FloatingAction
import com.hihihihi.domain.repository.FloatingActionRepository
import javax.inject.Inject

class SendFloatingActionUseCase @Inject constructor(
    private val repository: FloatingActionRepository
) {
    suspend operator fun invoke(action: FloatingAction) {
        repository.sendFloatingAction(action)
    }
}