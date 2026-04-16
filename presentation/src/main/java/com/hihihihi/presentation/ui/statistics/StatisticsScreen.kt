package com.hihihihi.presentation.ui.statistics

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.hihihihi.domain.model.DateRange
import com.hihihihi.domain.model.DateRangePreset
import com.hihihihi.domain.usecase.statistics.presetToRange
import com.hihihihi.presentation.R
import com.hihihihi.presentation.designsystem.components.Semi14Text
import com.hihihihi.presentation.designsystem.components.Semi16Text
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.ui.statistics.components.CategoryCard
import com.hihihihi.presentation.ui.statistics.components.EmptyCard
import com.hihihihi.presentation.ui.statistics.components.ReadingPageCard
import com.hihihihi.presentation.ui.statistics.components.ReadingTimeCard
import com.hihihihi.presentation.ui.statistics.components.StatisticsPicker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    initialPreset: DateRangePreset = DateRangePreset.WEEK,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(initialPreset) {
        viewModel.loadStatistics(initialPreset)
    }

    StatisticsContent(
        category = uiState.category,
        time = uiState.time,
        pages = uiState.pages,
        xLabels = uiState.xLabels,
        hasError = uiState.hasError,
        showPicker = uiState.showPicker,
        selectedPreset = uiState.selectedPreset,
        onShowPicker = viewModel::showPicker,
        onHidePicker = viewModel::hidePicker,
        onSetPreset = viewModel::setPreset,
    )
}

@Composable
private fun StatisticsContent(
    category: List<PieEntry>,
    time: List<BarEntry>,
    pages: List<Entry>,
    xLabels: List<String>,
    hasError: Boolean,
    showPicker: Boolean,
    selectedPreset: DateRangePreset,
    onShowPicker: () -> Unit,
    onHidePicker: () -> Unit,
    onSetPreset: (DateRangePreset) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val rangeText = remember(selectedPreset) { formatRange(selectedPreset) }
    val title = remember(selectedPreset) { pagesTitle(selectedPreset) }

    if (showPicker) {
        StatisticsPicker(
            initialIndex = DateRangePreset.entries.indexOf(selectedPreset).coerceAtLeast(0),
            items = STAT_PRESET_LABELS,
            onDismiss = onHidePicker,
            onConfirm = { index -> onSetPreset(DateRangePreset.entries.getOrElse(index) { DateRangePreset.WEEK }) },
            infiniteScroll = false,
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        contentPadding = PaddingValues(vertical = 20.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Semi14Text(
                    text = rangeText,
                    color = GureumTheme.colors.gray700,
                )
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onShowPicker,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Semi16Text(
                        STAT_PRESET_LABELS[DateRangePreset.entries.indexOf(selectedPreset).coerceAtLeast(0)],
                        color = GureumTheme.colors.gray700,
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_down),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = GureumTheme.colors.gray800,
                    )
                }
            }
        }

        item {
            Semi16Text("독서 장르 분포")
            Spacer(modifier = Modifier.height(12.dp))
            when {
                hasError -> EmptyCard("통계를 불러올 수 없습니다", "잠시 후 다시 시도해주세요")
                category.isEmpty() -> EmptyCard()
                else -> CategoryCard(entries = category)
            }
        }

        item {
            Semi16Text("독서 시간 분포")
            Spacer(modifier = Modifier.height(12.dp))
            when {
                hasError -> EmptyCard("통계를 불러올 수 없습니다", "잠시 후 다시 시도해주세요")
                time.isEmpty() || time.all { it.y == 0f } -> EmptyCard(subText = "새 기록을 추가하면 추이가 표시돼요.")
                else -> ReadingTimeCard(entries = time)
            }
        }

        item {
            Semi16Text(title)
            Spacer(modifier = Modifier.height(12.dp))
            when {
                hasError -> EmptyCard("통계를 불러올 수 없습니다", "잠시 후 다시 시도해주세요")
                pages.isEmpty() || pages.all { it.y == 0f } -> EmptyCard(subText = "책을 읽고 페이지를 기록해 주세요.")
                else -> ReadingPageCard(entries = pages, xLabels = xLabels)
            }
        }
    }
}

private fun formatRange(preset: DateRangePreset, now: LocalDateTime = LocalDateTime.now()): String {
    val range: DateRange = presetToRange(preset, now)
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    return "${range.start.toLocalDate().format(formatter)}~${range.end.toLocalDate().format(formatter)}"
}

private fun pagesTitle(preset: DateRangePreset) = when (preset) {
    DateRangePreset.WEEK -> "주간 독서 페이지"
    DateRangePreset.MONTH -> "월간 독서 페이지"
    DateRangePreset.THREE_MONTH -> "3개월 독서 페이지"
    DateRangePreset.SIX_MONTH -> "6개월 독서 페이지"
    DateRangePreset.YEAR -> "연간 독서 페이지"
}

private val STAT_PRESET_LABELS = listOf("1주", "1개월", "3개월", "6개월", "1년")

@Preview(name = "Empty - Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Empty - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatisticsEmptyPreview() {
    GureumPageTheme {
        StatisticsContent(
            category = emptyList(),
            time = emptyList(),
            pages = emptyList(),
            xLabels = emptyList(),
            hasError = false,
            showPicker = false,
            selectedPreset = DateRangePreset.WEEK,
            onShowPicker = {},
            onHidePicker = {},
            onSetPreset = {},
        )
    }
}

@Preview(name = "WithData - Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "WithData - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatisticsWithDataPreview() {
    val sampleCategory = listOf(
        PieEntry(40f, "소설"),
        PieEntry(30f, "자기계발"),
        PieEntry(20f, "경제"),
        PieEntry(10f, "기타"),
    )
    val sampleTime = listOf(
        BarEntry(0f, 30f),
        BarEntry(1f, 45f),
        BarEntry(2f, 10f),
        BarEntry(3f, 60f),
        BarEntry(4f, 20f),
        BarEntry(5f, 0f),
        BarEntry(6f, 15f),
    )
    val samplePages = listOf(
        Entry(0f, 100f),
        Entry(1f, 150f),
        Entry(2f, 120f),
        Entry(3f, 200f),
        Entry(4f, 180f),
        Entry(5f, 250f),
        Entry(6f, 220f),
    )
    val sampleXLabels = listOf("월", "화", "수", "목", "금", "토", "일")

    GureumPageTheme {
        StatisticsContent(
            category = sampleCategory,
            time = sampleTime,
            pages = samplePages,
            xLabels = sampleXLabels,
            hasError = false,
            showPicker = false,
            selectedPreset = DateRangePreset.WEEK,
            onShowPicker = {},
            onHidePicker = {},
            onSetPreset = {},
        )
    }
}
