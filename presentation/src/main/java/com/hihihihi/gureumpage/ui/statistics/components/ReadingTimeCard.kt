package com.hihihihi.gureumpage.ui.statistics.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.hihihihi.gureumpage.common.utils.PercentageOfTotalFormatter
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.statistics.mockReading

@Composable
fun ReadingTimeCard(
    modifier: Modifier = Modifier,
    labelColor: Int = GureumTheme.colors.gray600.toArgb(),
    entries: List<BarEntry>
) {
    GureumCard(
        modifier = modifier.height(210.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            factory = { context ->
                HorizontalBarChart(context).apply {

                    setExtraOffsets(0f, 0f, 80f, 0f)
                    setDrawValueAboveBar(true)      // 바 바깥에 숫자 표시

                    description.isEnabled = false   // 설명 표시 여부
                    legend.isEnabled = false        // 범례 표시 여부
                    setTouchEnabled(false)          // 전체 영역 터치
                    setDrawGridBackground(false)    // 백그라운드 컬러

                    val labels = listOf("새벽", "아침", "점심", "저녁", "밤").reversed()
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM // 라벨 좌측 정렬
                        granularity = 1f            // 라벨 거리 단위
                        setDrawGridLines(false)     // 가로 줄 끄기
                        setDrawAxisLine(false)      // 우측 끝 세로 줄 끄기
                        valueFormatter = IndexAxisValueFormatter(labels) // 라벨 매핑
                        textSize = 10f
                        textColor = labelColor
                    }

                    axisLeft.apply {
                        isEnabled = false
                        axisMinimum = 0f        // 최소치
                        setDrawGridLines(false) // 그리드 표시 여부
                        granularity = 10f
                        textColor = labelColor
                    }

                    axisRight.isEnabled = false // 하단 눈금 제거
                }
            },

            update = { chart ->
                val total = entries.sumOf { it.y.toDouble() }.toFloat()
                val dataSet = BarDataSet(entries, "독서 시간 분포").apply {
                    colors = ColorTemplate.LIBERTY_COLORS.toList()

                    setDrawValues(true)         // 숫자 퍼센트 표시
                    valueTextColor = labelColor // 숫자 컬러
                    valueTextSize = 12f         // 숫자 사이즈
                    valueFormatter = PercentageOfTotalFormatter(total) // 숫자 포맷

                    isHighlightEnabled = false  // 터치 강조
                }

                chart.apply {
                    data = BarData(dataSet).apply {
                        barWidth = 0.6f             // 바 두께
                    }
                    animateY(800, Easing.EaseInOutQuad)
                    notifyDataSetChanged()
                    invalidate()
                }
            }
        )
    }
}

@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "DarkMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GureumCardPreview() {
    GureumPageTheme {
//        ReadingTimeCard()
    }
}