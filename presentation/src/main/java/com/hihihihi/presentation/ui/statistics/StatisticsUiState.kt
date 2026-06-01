package com.hihihihi.presentation.ui.statistics

import androidx.compose.runtime.Immutable
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.hihihihi.domain.model.DateRangePreset

@Immutable
sealed interface StatisticsUiState {
    @Immutable
    data object Loading : StatisticsUiState

    @Immutable
    data class Content(
        val category: List<PieEntry> = emptyList(),
        val time: List<BarEntry> = emptyList(),
        val pages: List<Entry> = emptyList(),
        val xLabels: List<String> = emptyList(),
        val showPicker: Boolean = false,
        val selectedPreset: DateRangePreset = DateRangePreset.WEEK,
    ) : StatisticsUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : StatisticsUiState
}

internal fun StatisticsUiState.contentOrDefault(): StatisticsUiState.Content = when (this) {
    is StatisticsUiState.Content -> this
    is StatisticsUiState.Error -> previous ?: StatisticsUiState.Content()
    StatisticsUiState.Loading -> StatisticsUiState.Content()
}
