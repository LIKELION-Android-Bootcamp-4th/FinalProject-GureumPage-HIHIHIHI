package com.hihihihi.presentation.ui.home

import androidx.compose.runtime.Immutable
import com.hihihihi.domain.model.NotificationSettings
import com.hihihihi.presentation.ui.model.HomeUiModel

@Immutable
sealed interface HomeUiState {
    @Immutable
    data object Loading : HomeUiState

    @Immutable
    data class Content(
        val homeUiModel: HomeUiModel? = null,
        val notificationSettings: NotificationSettings = NotificationSettings(),
    ) : HomeUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : HomeUiState
}

internal fun HomeUiState.contentOrDefault(): HomeUiState.Content = when (this) {
    is HomeUiState.Content -> this
    is HomeUiState.Error -> previous ?: HomeUiState.Content()
    HomeUiState.Loading -> HomeUiState.Content()
}
