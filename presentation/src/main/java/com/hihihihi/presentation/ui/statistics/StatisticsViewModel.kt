package com.hihihihi.presentation.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.hihihihi.domain.model.DateRangePreset
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.statistics.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState

    val userId: String? = getCurrentUserIdUseCase()


    init {
        if (userId != null) {
            loadStatistics(DateRangePreset.WEEK)
        }
    }

    fun loadStatistics(preset: DateRangePreset) {
        if (userId == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getStatisticsUseCase(userId, preset).collect { statistics ->
                    _uiState.value = StatisticsUiState(
                        category = statistics.category.map { PieEntry(it.value, it.label) },
                        time = statistics.time.asReversed()
                            .mapIndexed { index, slice -> BarEntry(index.toFloat(), slice.value) },
                        pages = statistics.pages.map { Entry(it.x, it.y) },
                        xLabels = statistics.xLabels,
                    )
                }
            } catch (e: Exception) {
                _uiState.value = StatisticsUiState(
                    hasError = true,
                    errorMessage = "통계를 불러올 수 없습니다"
                )
            }
        }
    }
}
