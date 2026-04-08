package com.hihihihi.presentation.ui.statistics

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry

data class StatisticsUiState(
    val category: List<PieEntry> = emptyList(),
    val time: List<BarEntry> = emptyList(),
    val pages: List<Entry> = emptyList(),
    val xLabels: List<String> = emptyList(),
    val hasError: Boolean = false,
    val errorMessage: String = "",
    val showPicker: Boolean = false,
    val selectedPresetIndex: Int = 0,
)
