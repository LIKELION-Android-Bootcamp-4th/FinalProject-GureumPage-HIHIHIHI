package com.hihihihi.gureumpage.ui.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MyPageCalenderSection(
    stats: Map<LocalDate, Int>
) {
    val typography = GureumTypography
    val colors = GureumTheme.colors

    //자정 지나면 갱신
    val today = rememberToday()

    // 달 today 기준으로 계산(자동 갱신 대응)
    val currentMonth = YearMonth.from(today)
    val startMonth = currentMonth.minusMonths(100)
    val endMonth = currentMonth.plusMonths(100)

    // 우측 1/3부터 보이도록: 최근 4개월 정도를 기본 가시 시작점으로 잡음
    val rightOneThirdAnchorMonths = 4L

    //요일 시작: 일/월 자동 적용
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "독서 잔디",
            style = typography.titleLarge,
            color = colors.gray700,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        key(today) {
            val state = rememberHeatMapCalendarState(
                startMonth = startMonth,
                endMonth = endMonth,
                firstVisibleMonth = currentMonth.minusMonths(rightOneThirdAnchorMonths),
                firstDayOfWeek = firstDayOfWeek
            )
            HeatMapCalendar(
                state = state,
                dayContent = { calendarDay, _ ->
                    ///해당 날짜에 읽은 페이지 수(없으면 0)
                    val pages = stats[calendarDay.date] ?: 0
                    DayCell(
                        day = calendarDay,
                        today = today,
                        level = valueToLevel(pages)
                    )
                },
                weekHeader = { weekDays ->
                    WeekHeader(weekDays)
                },
                monthHeader = { calendarMonth ->
                    MonthHeader(calendarMonth)
                }
            )
        }
        HeatLegendWithLabel()
    }
}


@Composable
fun DayCell(day: CalendarDay, today: LocalDate, level: Int) {
    val colors = GureumTheme.colors

    val color = when (level) {
        0 -> colors.heatCell0
        1 -> colors.heatCell1
        2 -> colors.heatCell2
        3 -> colors.heatCell3
        else -> colors.heatCell4
    }

    Box(
        modifier = Modifier
            .size(16.dp)
            .padding(2.dp)
            .background(color = color, shape = RoundedCornerShape(2.dp))
            // 오늘일 때 빨간 테두리
            .then(
                if (day.date == today) Modifier.border(
                    width = 0.5.dp,
                    color = colors.systemRed,
                    shape = RoundedCornerShape(2.dp)
                ) else Modifier
            ),
    )
}

fun valueToLevel(value: Int): Int {
    return when {
        value <= 0 -> 0
        value <= 10 -> 1
        value <= 20 -> 2
        value <= 30 -> 3
        else -> 4
    }
}

@Composable
fun HeatLegendWithLabel() {
    val colors = GureumTheme.colors

    // TODO 색은 수정이 필요하긴 함니다..
    val levels = listOf(
        "0" to colors.heatCell0,
        "10p" to colors.heatCell1,
        "20p" to colors.heatCell2,
        "30p" to colors.heatCell3,
        "31p+" to colors.heatCell4
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            levels.forEach { (label, color) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, shape = RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        style = GureumTypography.labelSmall,
                        color = colors.gray400
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun WeekHeader(dayOfWeek: DayOfWeek) {
    val colors = GureumTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 3.5.dp, end = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val label = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

        Text(
            text = label,
            style = GureumTypography.labelSmall,
            color = colors.gray400,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MonthHeader(month: CalendarMonth) {
    val colors = GureumTheme.colors

    // 1월일때만 연도 붙여주기
    val text = if (month.yearMonth.month == Month.JANUARY) {
        "${month.yearMonth.year}년 ${month.yearMonth.monthValue}월"
    } else {
        "${month.yearMonth.monthValue}월"
    }

    Text(
        text = text,
        style = GureumTypography.labelSmall,
        color = colors.gray500,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun rememberToday(): LocalDate {
    var today by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = java.time.ZonedDateTime.now()
            val nextMidnight = now.toLocalDate()
                .plusDays(1)
                .atStartOfDay(now.zone)
            val delayMs = java.time.Duration.between(now, nextMidnight).toMillis()
            kotlinx.coroutines.delay(delayMs)
            today = LocalDate.now() // 자정 통과 시 갱신
        }
    }
    return today
}