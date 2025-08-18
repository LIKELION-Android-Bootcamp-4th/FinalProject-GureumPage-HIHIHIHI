package com.hihihihi.gureumpage.ui.statistics

import android.content.res.Configuration
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
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.DateRange
import com.hihihihi.domain.model.DateRangePreset
import com.hihihihi.domain.usecase.statistics.presetToRange
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.Semi14Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.statistics.components.CategoryCard
import com.hihihihi.gureumpage.ui.statistics.components.GureumStatisticsPicker
import com.hihihihi.gureumpage.ui.statistics.components.ReadingPageCard
import com.hihihihi.gureumpage.ui.statistics.components.ReadingTimeCard
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val scrollState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPicker by remember { mutableStateOf(false) }
    var presetIndex by rememberSaveable { mutableStateOf(0) }
    val preset = presetFromIndex(presetIndex)
    val rangeText = remember(presetIndex) { formatRange(preset) }


    if (showPicker) {
        GureumStatisticsPicker(
            initialIndex = presetIndex,
            items = STAT_PRESET_LABELS,
            onDismiss = { showPicker = false },
            onConfirm = {
                index -> presetIndex = index
                showPicker = false
                viewModel.loadStatistics(presetFromIndex(index))
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        contentPadding = PaddingValues(vertical = 20.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Semi14Text(
                    text = rangeText,
                    color = GureumTheme.colors.gray700
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { showPicker = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Semi16Text(STAT_PRESET_LABELS[presetIndex], color = GureumTheme.colors.gray700)
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_down),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = GureumTheme.colors.gray800
                        )
                    }
                }
            }
        }

        item {
            Semi16Text("독서 장르 분포")
            Spacer(modifier = Modifier.height(12.dp))
            CategoryCard(entries = uiState.category)
        }

        item {
            Semi16Text("독서 시간 분포")
            Spacer(modifier = Modifier.height(12.dp))
            ReadingTimeCard(entries = uiState.time)
        }

        item {
            Semi16Text("주간 독서 페이지")
            Spacer(modifier = Modifier.height(12.dp))
            ReadingPageCard(entries = uiState.pages, xLabels = uiState.xLabels)
        }
    }
}

fun formatRange(preset: DateRangePreset, now: LocalDateTime = LocalDateTime.now()): String =
    formatRange(presetToRange(preset, now))

fun formatRange(range: DateRange): String {
    val fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    return "${range.start.toLocalDate().format(fmt)}~${range.end.toLocalDate().format(fmt)}"
}

val STAT_PRESET_LABELS = listOf("1주", "1개월", "3개월", "6개월", "1년")

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatisticsPreview() {
    GureumPageTheme {
        StatisticsScreen()
    }
}

fun presetFromIndex(index: Int) = when (index) {
    0 -> DateRangePreset.WEEK
    1 -> DateRangePreset.MONTH
    2 -> DateRangePreset.THREE_MONTH
    3 -> DateRangePreset.SIX_MONTH
    else -> DateRangePreset.YEAR
}