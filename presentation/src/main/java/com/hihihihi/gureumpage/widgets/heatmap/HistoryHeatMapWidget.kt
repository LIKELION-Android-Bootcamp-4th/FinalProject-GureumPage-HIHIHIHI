package com.hihihihi.gureumpage.widgets.heatmap

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
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
                .getOrNull() ?: HeatPayload(monthLeft = "", monthRight = "", levels = emptyList())

            payload.levels.sortedBy { it.ymd }

            Log.d("HistoryHeatMapWidget", "payload:$payload")

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Box(
                    modifier = GlanceModifier
                        .height(180.dp)
                        .width(180.dp)
                        .padding(10.dp)
                        .background(
                            ColorProvider(R.color.heat_bg)
                        ).clickable(
                            onClick = actionRunCallback<OpenAppAction>(
                                parameters = actionParametersOf()
                            )
                        ),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        // 헤더(월 표시)
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(GlanceModifier.width(16.dp))
                            Text(
                                text = payload.monthLeft,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ColorProvider(R.color.heat_label)
                                ),

                                modifier = GlanceModifier.defaultWeight()
                            )
                            Text(
                                text = payload.monthRight,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ColorProvider(R.color.heat_label)
                                ),

                                modifier = GlanceModifier.defaultWeight()
                            )
                        }

                        Spacer(GlanceModifier.height(6.dp))
                        Column {
                            // 본문 : 2달 (요일 7행 × 9열)
                            for (row in 0 until 7) {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth()
                                        .padding(bottom = if (row < 6) 4.dp else 0.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 요일 라벨
                                    Text(
                                        text = Yoil[row],
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = ColorProvider(R.color.heat_label)
                                        ),

                                        modifier = GlanceModifier.width(16.dp)
                                    )
                                    // 9주 셀
                                    for (col in 0 until 9) {
                                        val idx = row * 9 + col // row-major
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

    val size = 14.dp
    val radius = 3.dp
    val borderWidth = 1.dp // 1dp는 위젯 렌더링에서 잘 안 보일 수 있음

    val colorRes = when (cell.level.coerceIn(0, 4)) {
        0 -> R.color.heat_cell_0
        1 -> R.color.heat_cell_1
        2 -> R.color.heat_cell_2
        3 -> R.color.heat_cell_3
        else -> R.color.heat_cell_4
    }

    Box(modifier = GlanceModifier.padding(horizontal = 1.dp)) {
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
                    date?.let {
                        Text(
                            text = it.dayOfMonth.toString(),
                            style = TextStyle(
                                color = ColorProvider(android.R.color.black),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = GlanceModifier
                    .size(size)
                    .cornerRadius(radius)
                    .background(ColorProvider(colorRes))
            ) {
//                date?.let {
//                    Text(
//                        text = it.dayOfMonth.toString(),
//                        style = TextStyle(
//                            color = ColorProvider(android.R.color.black),
//                            fontSize = 10.sp,
//                            fontWeight = FontWeight.Normal
//                        )
//                    )
//                }
            }
        }
    }
}

/** 위젯 데이터 구조 */
data class HeatCell(val ymd: String = "", val level: Int = 0)
data class HeatPayload(
    val monthLeft: String = "",
    val monthRight: String = "",
    val levels: List<HeatCell> = emptyList()
)
