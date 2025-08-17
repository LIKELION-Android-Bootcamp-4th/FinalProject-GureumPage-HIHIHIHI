package com.hihihihi.gureumpage.ui.statistics.components

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.hihihihi.gureumpage.common.utils.SimplePercentFormatter
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    labelColor: Int = GureumTheme.colors.gray600.toArgb(),
    entries: List<PieEntry>,
) {
    GureumCard(modifier = modifier.heightIn(210.dp, 250.dp)) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 18.dp),
            factory = { context ->
                PieChart(context).apply {
                    description.isEnabled = false   // 우측 하단 차트 설명 숨김
                    setDrawEntryLabels(false)       // 조각 라벨 보이지 않음
                    setHoleColor(Color.TRANSPARENT) // 중앙 홀 컬러
                    holeRadius = 70f                // 중앙 홀 크기
                    transparentCircleRadius = 0f    // 중앙 반투명 홀 크기

                    // 범례 설정
                    legend.apply {
                        isEnabled = true                // 보이기
                        horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER // 중앙 정렬
                        form = Legend.LegendForm.CIRCLE // 범례 컬러 모양
                        textColor = labelColor          // 텍스트 컬러
                    }
                }
            },
            update = { chart ->
                val dataSet = PieDataSet(entries, "").apply {
                    colors = ColorTemplate.LIBERTY_COLORS.toList() // 보이는 컬러 목록
                    sliceSpace = 0f // 조각 간 간격

                    valueTextSize = 12f                 // 숫자 크기
                    valueTextColor = labelColor         // 숫자 컬러
                    valueFormatter = SimplePercentFormatter() // 숫자 포맷터
                    selectionShift = 3f                 // 조각 클릭 시 튀어나오는 범위 (클릭 이벤트랑 같이 사용하기)
                    yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE // 숫자 사이드에 표시

                    valueLinePart1Length = 0.55f // 조각과 이어지는 라인 길이
                    valueLinePart2Length = 0.15f // 숫자와 이어지는 라인 길이
                    valueLinePart1OffsetPercentage = 120f // 라인 시작 위치

                    setAutomaticallyDisableSliceSpacing(true)   // 조각 간 간격 자동 조정
                    isUsingSliceColorAsValueLineColor = true    // 조각 색과 라인 컬러 통일
                }

                chart.apply {
                    setExtraOffsets(10f, 10f, 10f, 8f)
                    setUsePercentValues(true)           // 퍼센트로 표시
                    data = PieData(dataSet)
                    animateY(1200, Easing.EaseInOutQuad)
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
//        GenreCard()
    }
}