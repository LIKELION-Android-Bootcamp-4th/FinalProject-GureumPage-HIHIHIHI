package com.hihihihi.domain.repository

import com.hihihihi.domain.model.TimerState
import kotlinx.coroutines.flow.Flow

interface TimerRepository {

    suspend fun saveTimerState(state: TimerState)

    suspend fun getTimerState(): TimerState

    suspend fun clearTimerState()

    suspend fun restoreTimerState(): TimerState

    fun observeTimerState(): Flow<TimerState>

}