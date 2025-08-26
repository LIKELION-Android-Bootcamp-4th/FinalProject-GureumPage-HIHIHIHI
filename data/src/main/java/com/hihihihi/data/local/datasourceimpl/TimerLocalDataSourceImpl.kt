package com.hihihihi.data.local.datasourceimpl

import android.content.Context
import com.google.gson.Gson
import com.hihihihi.data.local.datasource.TimerLocalDataSource
import com.hihihihi.domain.model.TimerState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TimerLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
): TimerLocalDataSource {
    companion object {
        private const val TIMER_PREFS ="timer_state_prefs"
        private const val TIMER_STATE_KEY = "timer_state"
        private const val KEY_LAST_SAVED_TIME = "last_saved_time"
    }

    override suspend fun saveTimerState(state: TimerState, lastSavedTime: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getTimerState(): Pair<TimerState?, Long> {
        TODO("Not yet implemented")
    }

    override suspend fun clearTimerState() {
        TODO("Not yet implemented")
    }
}