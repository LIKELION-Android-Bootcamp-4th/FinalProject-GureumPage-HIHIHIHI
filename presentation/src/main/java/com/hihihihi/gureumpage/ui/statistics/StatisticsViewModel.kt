package com.hihihihi.gureumpage.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.DateRangePreset
import com.hihihihi.domain.usecase.statistics.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getStatisticsUseCase: GetStatisticsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState

    val userId = FirebaseAuth.getInstance().currentUser!!.uid


    init {
        loadStatistics(DateRangePreset.WEEK)
    }

    fun loadStatistics(preset: DateRangePreset) {
        viewModelScope.launch(Dispatchers.IO) {
            getStatisticsUseCase(userId, preset).collect { statistics ->
                _uiState.value = StatisticsUiState(
                    category = statistics.category.map { PieEntry(it.value, it.label) },
                    time = statistics.time.asReversed()
                        .mapIndexed { index, slice -> BarEntry(index.toFloat(), slice.value) },
                    pages = statistics.pages.map { Entry(it.x, it.y) },
                    xLabels = statistics.xLabels,
                )
            }
        }
    }
}