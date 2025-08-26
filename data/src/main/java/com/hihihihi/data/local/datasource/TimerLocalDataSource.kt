package com.hihihihi.data.local.datasource

import com.hihihihi.domain.model.TimerState

interface TimerLocalDataSource {
    suspend fun saveTimerState(state: TimerState, lastSavedTime: Long)

    suspend fun getTimerState(): Pair<TimerState?, Long>

    suspend fun clearTimerState()
}