package com.hihihihi.domain.usecase.timer

import com.hihihihi.domain.model.FloatingAction
import com.hihihihi.domain.repository.FloatingActionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFloatingActionsUseCase @Inject constructor(
    private val repository: FloatingActionRepository
) {
    operator fun invoke(): Flow<FloatingAction> {
        return repository.observeFloatingActions()
    }
}