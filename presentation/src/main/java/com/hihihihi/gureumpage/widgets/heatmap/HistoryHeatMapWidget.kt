package com.hihihihi.gureumpage.widgets.heatmap

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.google.gson.Gson
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.widgets.common.actions.OpenAppAction
import java.time.LocalDate

class HistoryHeatMapWidget : GlanceAppWidget() {

    companion object {
        const val WIDGET_DATA_KEY = "heat_map_widget_data"
        private val Yoil = listOf("일", "월", "화", "수", "목", "금", "토")
    }

    override val sizeMode: SizeMode = SizeMode.Exact

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val state = currentState<Preferences>()
            val json = state[stringPreferencesKey(WIDGET_DATA_KEY)].orEmpty()
            val payload = runCatching { Gson().fromJson(json, HeatPayload::class.java) }
                .getOrNull() ?: HeatPayload(monthHeaders = emptyList(), levels = emptyList())

            payload.levels.sortedBy { it.ymd }

            Column(
                modifier = GlanceModifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Box(
                    modifier = GlanceModifier
                        .height(160.dp)
                        .width(160.dp)
                        .padding(10.dp)
                        .background(
                            ColorProvider(R.color.heat_bg)
                        ).clickable(
                            onClick = actionRunCallback<OpenAppAction>(
                                parameters = actionParametersOf()
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalAlignment = Alignment.Top,
                    ) {
                        // 월 헤더 (1일이 포함된 열 위에만 표시)
                        Row(
                            modifier = GlanceModifier.fillMaxWidth()
                        ) {
                            Spacer(GlanceModifier.width(12.dp)) // 요일 라벨 공간

                            for (col in 0 until 8) {
                                val monthHeader = payload.monthHeaders.getOrNull(col) ?: ""
                                Box(
                                    modifier = GlanceModifier.width(16.dp)
                                        .padding(horizontal = 1.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (monthHeader.isNotEmpty()) {
                                        Text(
                                            text = monthHeader,
                                            style = TextStyle(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = ColorProvider(R.color.heat_label)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Row {
                            // 요일 라벨을 세로로 표시
                            Column(
                                modifier = GlanceModifier.padding(end = 4.dp)
                            ) {
                                for (day in Yoil) {
                                    Text(
                                        text = day,
                                        style = TextStyle(
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = ColorProvider(R.color.heat_label)
                                        ),
                                        modifier = GlanceModifier
                                            .height(15.dp)
                                        // .padding(bottom = 1.dp)
                                    )
                                }
                            }

                            // 본문: 2달 (9주 × 7일) - 세로로 일주일 표시
                            Row {
                                for (col in 0 until 8) {
                                    Column(
                                        modifier = GlanceModifier
                                            .padding(end = if (col < 7) 1.dp else 0.dp)
                                    ) {
                                        for (row in 0 until 7) {
                                            val idx = col * 7 + row
                                            val cell = payload.levels.getOrNull(idx) ?: HeatCell()
                                            HeatCellBox(cell)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun HeatCellBox(cell: HeatCell) {
        val zone = java.time.ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val date = runCatching { LocalDate.parse(cell.ymd) }.getOrNull()
        val isToday = date == today

        val size = 13.dp
        val radius = 2.dp
        val borderWidth = 1.dp // 1dp는 위젯 렌더링에서 잘 안 보일 수 있음

        val colorRes = when (cell.level.coerceIn(0, 4)) {
            0 -> R.color.heat_cell_0
            1 -> R.color.heat_cell_1
            2 -> R.color.heat_cell_2
            3 -> R.color.heat_cell_3
            else -> R.color.heat_cell_4
        }

        Box(modifier = GlanceModifier.padding(1.dp)) {
            if (isToday) {
                Box(
                    modifier = GlanceModifier
                        .size(size)
                        .cornerRadius(radius)
                        .background(ColorProvider(R.color.systemRed)),
                    contentAlignment = Alignment.Center
                ) {
                    val innerSize = size - (borderWidth * 2)
                    val innerRadius = (radius - borderWidth).coerceAtLeast(0.dp)
                    Box(
                        modifier = GlanceModifier
                            .size(innerSize)
                            .cornerRadius(innerRadius)
                            .background(ColorProvider(colorRes)),
                        contentAlignment = Alignment.Center
                    ) {
//                        date?.let {
//                            Text(
//                                text = it.dayOfMonth.toString(),
//                                style = TextStyle(
//                                    color = ColorProvider(android.R.color.black),
//                                    fontSize = 10.sp,
//                                    fontWeight = FontWeight.Normal
//                                )
//                            )
//                        }
                    }
                }
            } else {
                Box(
                    modifier = GlanceModifier
                        .size(size)
                        .cornerRadius(radius)
                        .background(ColorProvider(colorRes))
                ) {
//                    date?.let {
//                        Text(
//                            text = it.dayOfMonth.toString(),
//                            style = TextStyle(
//                                color = ColorProvider(android.R.color.black),
//                                fontSize = 8.sp,
//                                fontWeight = FontWeight.Normal
//                            )
//                        )
//                    }
                }
            }
        }
    }
}

/** 위젯 데이터 구조 */
data class HeatCell(val ymd: String = "", val level: Int = 0)

data class HeatPayload(
    val monthHeaders: List<String> = emptyList(),
    val levels: List<HeatCell> = emptyList()
)
