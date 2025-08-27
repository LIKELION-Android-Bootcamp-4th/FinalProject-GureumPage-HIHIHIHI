package com.hihihihi.domain.repository

import com.hihihihi.domain.model.FloatingAction
import kotlinx.coroutines.flow.Flow

interface FloatingActionRepository {

    suspend fun sendFloatingAction(action: FloatingAction)

    fun observeFloatingActions(): Flow<FloatingAction>
}