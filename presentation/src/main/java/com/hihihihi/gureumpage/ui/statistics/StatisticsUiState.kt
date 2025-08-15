package com.hihihihi.gureumpage.ui.statistics

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.hihihihi.domain.model.CategorySlice

data class StatisticsUiState(
    val category: List<PieEntry> = emptyList(),
    val time: List<BarEntry> = emptyList(),
    val pages: List<Entry> = emptyList(),
    val xLabels: List<String> = emptyList()
)
