package com.hihihihi.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
    val networkState: StateFlow<Boolean>
    val showNetworkWarning: StateFlow<Boolean>

    fun checkCurrentNetwork(): Boolean
    fun dismissNetworkWarning()
}
