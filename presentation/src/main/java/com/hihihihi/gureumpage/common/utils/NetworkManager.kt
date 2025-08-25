package com.hihihihi.gureumpage.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@Singleton
class NetworkManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkState = MutableStateFlow(true)
    val networkState: StateFlow<Boolean> = _networkState.asStateFlow()

    private val _showNetworkWarning = MutableStateFlow(false)
    val showNetworkWarning: StateFlow<Boolean> = _showNetworkWarning.asStateFlow()


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: android.net.Network) {
            _networkState.value = true
            _showNetworkWarning.value = false
        }

        override fun onLost(network: android.net.Network) {
            _networkState.value = false
            _showNetworkWarning.value = true
        }
    }

    init {
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }


    fun checkCurrentNetwork(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        _networkState.value = isConnected
        return isConnected
    }

    fun dismissNetworkWarning() {
        _showNetworkWarning.value = false
    }
}