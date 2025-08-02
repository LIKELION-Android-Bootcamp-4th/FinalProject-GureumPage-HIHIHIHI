package com.hihihihi.gureumpage.ui.statistics

import android.R.attr.textColor
import android.R.attr.textSize
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BaseEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.hihihihi.gureumpage.designsystem.components.TitleText
import com.hihihihi.gureumpage.designsystem.theme.GureumColors
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import java.text.NumberFormat

@Composable
fun StatisticsScreen() {
    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        state = scrollState,
    ) {
        item {
            val labelColor = if (GureumTheme.isDarkTheme) Color.LTGRAY else Color.DKGRAY

            TitleText("독서 장르 분포")
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(210.dp)
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 18.dp),
                    factory = { context ->
                        PieChart(context).apply {
                            val entries = mutableListOf<PieEntry>()

                            for (mockGenre in mockGenreList) {
                                entries.add(PieEntry(mockGenre.value, mockGenre.label))
                            }

                            val dataSet = PieDataSet(entries, "").apply {
                                colors = ColorTemplate.LIBERTY_COLORS.toList() // 보이는 컬러 목록
                                sliceSpace = 0f // 조각 간 간격
                                setExtraOffsets(0f, 4f, 0f, 8f)

                                valueTextSize = 12f // 숫자 크기
                                valueTextColor = labelColor // 숫자 컬러
                                valueFormatter = PercentFormatter() // 숫자 포맷터
                                setUsePercentValues(true) // 퍼센트로 표시
                                selectionShift = 3f // 조각 클릭 시 튀어나오는 범위 (클릭 이벤트랑 같이 사용하기)
                                yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE // 숫자 사이드에 표시

                                valueLinePart1Length = 0.55f // 조각과 이어지는 라인 길이
                                valueLinePart2Length = 0.15f // 숫자와 이어지는 라인 길이
                                valueLinePart1OffsetPercentage = 120f // 라인 시작 위치

                                setAutomaticallyDisableSliceSpacing(true) // 조각 간 간격 자동 조정
                                isUsingSliceColorAsValueLineColor = true // 조각 색과
                            }

                            data = PieData(dataSet)
                            description.isEnabled = false // 우측 하단 차트 설명 숨김
                            isRotationEnabled = false
                            setDrawEntryLabels(false) // 조각 라벨 보이지 않음
                            setHoleColor(Color.TRANSPARENT) /// 중앙 홀 컬러
                            holeRadius = 70f // 중앙 홀 크기
                            transparentCircleRadius = 0f // 중앙 반투명 홀 크기

                            // 범례 설정
                            legend.apply {
                                isEnabled = true // 보이기
                                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER // 중앙 정렬
                                form = Legend.LegendForm.CIRCLE // 범례 컬러 모양
                                textColor = labelColor // 텍스트 컬러
                            }
                            animateY(1200, Easing.EaseInOutQuad)
                        }
                    },
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            TitleText("독서 시간 분포")
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(210.dp)
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    factory = { context ->
                        HorizontalBarChart(context).apply {
                            val entries = mutableListOf<BarEntry>()

                            for (mock in mockReading) {
                                entries.add(BarEntry(mock.x, mock.y))
                            }

                            val dataSet = BarDataSet(entries, "독서 시간 분포").apply {
                                val total = entries.sumOf { it.y.toDouble() }.toFloat()

                                colors = ColorTemplate.LIBERTY_COLORS.toList()

                                setDrawValues(true) // 숫자 퍼센트 표시
                                valueTextColor = labelColor // 숫자 컬러
                                valueTextSize = 12f // 숫자 사이즈
                                valueFormatter = PercentFormatter(total) // 숫자 포맷

                                isHighlightEnabled = false // 터치 강조
                            }
                            setExtraOffsets(0f, 0f, 80f, 0f)
                            setDrawValueAboveBar(true) // 바 바깥에 숫자 표시

                            data = BarData(dataSet).apply {
                                barWidth = 0.6f // 바 두께
                            }

                            description.isEnabled = false // 설명 표시 여부
                            legend.isEnabled = false // 범례 표시 여부
                            setTouchEnabled(false) // 전체 영역 터치
                            setDrawGridBackground(false) // 백그라운드 컬러

                            animateY(800, Easing.EaseInOutQuad)

                            val labels = listOf("새벽", "아침", "점심", "저녁", "밤")
                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM // 라벨 좌측 정렬
                                granularity = 1f // 라벨 거리 단위
                                setDrawGridLines(false) // 가로 줄 끄기
                                setDrawAxisLine(false) // 우측 끝 세로 줄 끄기
                                valueFormatter = IndexAxisValueFormatter(labels) // 라벨 매핑
                                textSize = 10f
                                textColor = labelColor
                            }

                            axisLeft.apply {
                                isEnabled = false
                                axisMinimum = 0f // 최소치
                                setDrawGridLines(false) // 그리드 표시 여부
                                granularity = 10f
                                textColor = labelColor
                            }

                            axisRight.isEnabled = false // 하단 눈금 제거

                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            TitleText("주간 독서 페이지")
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(210.dp)
            ) {
//                val lineFillColor = if (GureumTheme.isDarkTheme) GureumColors.defaultDarkColors().primary.toArgb()
//                else GureumColors.defaultLightColors().primary.toArgb()

                val lineFillColor = GureumTheme.colors.primary50.toArgb()
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 18.dp),
                    factory = { context ->
                        LineChart(context).apply {
                            val entries = mutableListOf<Entry>()

                            mockPages.forEach { entries.add(Entry(it.x, it.y)) }

                            val dataSet = LineDataSet(entries, "주간 독서 페이지").apply {
                                color = labelColor
                                lineWidth = 1.5f // 선 두께
                                mode = LineDataSet.Mode.CUBIC_BEZIER // 선 모양

                                setDrawCircles(false) // 점 삭제
                                valueTextSize = 10f
                                valueTextColor = labelColor
//                                valueFormatter = PercentFormatter()

                                setDrawFilled(true) // 나쁘지 않음
                                fillAlpha = 30
                                fillDrawable = GradientDrawable(
                                    GradientDrawable.Orientation.TOP_BOTTOM,
                                    intArrayOf(lineFillColor, Color.TRANSPARENT)
                                )
                            }

                            data = LineData(dataSet)
                            description.isEnabled = false
                            setTouchEnabled(false)

                            val week = listOf("일", "월", "화", "수", "목", "금", "토")
                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                textSize = 12f
                                textColor = labelColor
                                valueFormatter = IndexAxisValueFormatter(week)
                            }

                            axisLeft.isEnabled = false
                            axisRight.isEnabled = false
                            legend.isEnabled = false
                        }
                    },
                )
            }
        }
    }
}

class PercentFormatter(private val total: Float = 0f) : ValueFormatter() {
    private val percentFormatter = NumberFormat.getPercentInstance().apply {
        minimumFractionDigits = 0
    }

    override fun getFormattedValue(value: Float): String? = percentFormatter.format(value / 100)
    override fun getBarLabel(barEntry: BarEntry): String {
        val percent = if (total == 0f) 0f else barEntry.y / total
        return percentFormatter.format(percent)
    }

}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatisticsPreview() {
    GureumPageTheme {
        StatisticsScreen()
    }
}