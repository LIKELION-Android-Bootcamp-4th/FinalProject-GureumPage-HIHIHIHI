package com.hihihihi.gureumpage.ui.statistics.components

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.hihihihi.gureumpage.common.utils.PageFormatter
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun ReadingPageCard(
    modifier: Modifier = Modifier,
    labelColor: Int = GureumTheme.colors.gray600.toArgb(),
    lineFillColor: Int = GureumTheme.colors.primary30.toArgb(),
    entries: List<Entry>,
    xLabels: List<String>
) {
    GureumCard(
        modifier = modifier.height(210.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 18.dp),
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false
                    setTouchEnabled(false)

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        textSize = 12f
                        textColor = labelColor
                    }

                    axisLeft.isEnabled = false
                    axisRight.isEnabled = false
                    legend.isEnabled = false
                }
            },
            update = { chart ->
                val dataSet = LineDataSet(entries, "주간 독서 페이지").apply {
                    color = labelColor
                    lineWidth = 1.5f // 선 두께
                    mode = LineDataSet.Mode.HORIZONTAL_BEZIER // 선 모양

                    setDrawCircles(false) // 점 삭제
                    valueTextSize = 10f
                    valueTextColor = labelColor
                    valueFormatter = PageFormatter()

                    setDrawFilled(true)
                    fillAlpha = 30
                    fillDrawable = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(lineFillColor, Color.TRANSPARENT)
                    )
                }

                chart.apply {
                    data = LineData(dataSet)
                    xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
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
//        ReadingPageCard()
    }
}