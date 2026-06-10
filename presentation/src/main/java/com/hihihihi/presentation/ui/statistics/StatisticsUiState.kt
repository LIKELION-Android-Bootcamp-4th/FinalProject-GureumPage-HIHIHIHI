package com.hihihihi.presentation.ui.statistics

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.hihihihi.domain.model.DateRangePreset

sealed interface StatisticsUiState {
    data object Loading : StatisticsUiState

    data class Content(
        val category: List<PieEntry> = emptyList(),
        val time: List<BarEntry> = emptyList(),
        val pages: List<Entry> = emptyList(),
        val xLabels: List<String> = emptyList(),
        val showPicker: Boolean = false,
        val selectedPreset: DateRangePreset = DateRangePreset.WEEK,
    ) : StatisticsUiState

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
