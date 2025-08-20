package com.hihihihi.domain.usecase.statistics

import com.hihihihi.domain.model.CategorySlice
import com.hihihihi.domain.model.DailyReadPage
import com.hihihihi.domain.model.DateRange
import com.hihihihi.domain.model.DateRangePreset
import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.Page
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.Statistics
import com.hihihihi.domain.model.TimeSlice
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.DailyReadPageRepository
import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.repository.UserBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val userBookRepository: UserBookRepository,
    private val historyRepository: HistoryRepository,
    private val dailyReadPageRepository: DailyReadPageRepository
) {
    operator fun invoke(
        userId: String,
        preset: DateRangePreset,
    ): Flow<Statistics> {
        val range = presetToRange(preset)

        val booksFlow = userBookRepository.getUserBooks(userId)
        val historiesFlow = historyRepository.getHistoriesByUserId(userId)
        val dailiesFlow = dailyReadPageRepository.getDailyReadPagesFlow(userId)

        return combine(booksFlow, historiesFlow, dailiesFlow) { books, histories, dailies ->
            try {
                // 카테고리 계산
                val category = try {
                    categoryFromUserBooks(books, range)
                } catch (ex: Exception) {
                    emptyList<CategorySlice>()
                }

                // 독서 시간 계산
                val time = try {
                    readingTime(histories, range)
                } catch (ex: Exception) {
                    emptyList<TimeSlice>()
                }

                // 페이지 계산
                val (pages, xLabels) = try {
                    readingPages(dailies, preset, range)
                } catch (ex: Exception) {
                    emptyList<Page>() to emptyList<String>()
                }

                // 최종 통계
                Statistics(category, time, pages, xLabels)
            } catch (e: Exception) {
                Statistics(emptyList(), emptyList(), emptyList(), emptyList())
            }
        }.distinctUntilChanged()
    }
}


// 기간 프리셋 설정
fun presetToRange(preset: DateRangePreset, now: LocalDateTime = LocalDateTime.now()): DateRange {
    val end = now.toLocalDate().atTime(LocalTime.MAX)
    val start = when (preset) {
        // asStartOfDay(): 해당 날짜의 시작 시간(00:00)을 반환
        DateRangePreset.WEEK -> end.toLocalDate().minusDays(6).atStartOfDay()
        DateRangePreset.MONTH -> end.toLocalDate().minusMonths(1).atStartOfDay()
        DateRangePreset.THREE_MONTH -> end.toLocalDate().minusMonths(3).atStartOfDay()
        DateRangePreset.SIX_MONTH -> end.toLocalDate().minusMonths(6).atStartOfDay()
        DateRangePreset.YEAR -> end.toLocalDate().minusYears(1).atStartOfDay()
    }
    return DateRange(start, end)
}

// 카테고리 분포 계산
private fun categoryFromUserBooks(books: List<UserBook>, range: DateRange): List<CategorySlice> {
    // 책의 종료일 또는 시작일이 주어진 범위 (1주, 1개월, 3개월 등) 안에 있는지 파악
    fun inRange(book: UserBook): Boolean {
        val pivot = book.endDate ?: book.startDate ?: return false // 비교할 값의 기준
        return !pivot.isBefore(range.start) && !pivot.isAfter(range.end)
    }
    return books
        .asSequence()
        .filter { it.status != ReadingStatus.PLANNED }  // 읽기 전 제외
        .filter(::inRange)
        .groupBy { it.category ?: "기타" }               // 카테고리 이름으로 그룹
        .map { (category, books) -> CategorySlice(category, books.size.toFloat()) }
        .sortedByDescending { it.value }
}

// 독서 시간 분포
private fun readingTime(histories: List<History>, range: DateRange): List<TimeSlice> {
    data class Bucket(val label: String, val start: LocalTime, val end: LocalTime)

    val buckets = listOf(
        Bucket("새벽", LocalTime.of(0, 0), LocalTime.of(6, 0)),
        Bucket("아침", LocalTime.of(6, 0), LocalTime.of(10, 0)),
        Bucket("점심", LocalTime.of(10, 0), LocalTime.of(14, 0)),
        Bucket("저녁", LocalTime.of(14, 0), LocalTime.of(20, 0)),
        Bucket("밤", LocalTime.of(20, 0), LocalTime.MIDNIGHT)
    )
    val acc = LongArray(buckets.size)

    histories.forEach { hist ->
        val baseStartDay = hist.startTime ?: return@forEach
        val baseEndDay = hist.endTime ?: return@forEach
        var startDay = if (baseStartDay.isBefore(range.start)) range.start else baseStartDay
        val endDay = if (baseEndDay.isAfter(range.end)) range.end else baseEndDay

        if (!startDay.isBefore(endDay)) return@forEach

        while (!startDay.toLocalDate().isAfter(endDay.toLocalDate())) {
            val day = startDay.toLocalDate()
            val segEnd = minOf(endDay, day.atTime(LocalTime.MAX))

            buckets.forEachIndexed { index, bucket ->
                val bucketStart = day.atTime(bucket.start)
                val bucketEnd =
                    if (bucket.end == LocalTime.MIDNIGHT) day.plusDays(1).atStartOfDay() else day.atTime(bucket.end)
                val startMax = maxOf(startDay, bucketStart)
                val endMin = minOf(segEnd, bucketEnd)

                if (startMax.isBefore(endMin)) {
                    val minutes = ChronoUnit.MINUTES.between(startMax, endMin)
                    acc[index] += minutes
                }
            }

            // 안전하게 하루 단위로 이동
            startDay = day.plusDays(1).atStartOfDay()
            if (!startDay.isBefore(endDay) && startDay != endDay) break // 혹시 무한루프 방지
        }
    }

    return buckets.mapIndexed { index, bucket ->
        TimeSlice(bucket.label, acc[index].toFloat())
    }
}

// 주간 독서 페이지
private fun readingPages(
    dailies: List<DailyReadPage>,
    preset: DateRangePreset,
    range: DateRange
): Pair<List<Page>, List<String>> {
    val byDate = dailies
        .filter { it.date.atStartOfDay() in range.start..range.end }
        .groupBy { it.date }
        .mapValues { (_, daily) -> daily.sumOf { it.totalReadPageCount }.toFloat() }

    val startDay = range.start.toLocalDate()
    val endDay = range.end.toLocalDate()

    fun dowIndex(d: DayOfWeek) = when (d) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
    }

    return when (preset) {
        // 일 ~ 토 요일 고정
        DateRangePreset.WEEK -> {
            val labels = listOf("일", "월", "화", "수", "목", "금", "토")
            val week = FloatArray(7)
            var currentDay = startDay
            while (!currentDay.isAfter(endDay)) {
                week[dowIndex(currentDay.dayOfWeek)] += byDate[currentDay] ?: 0f
                currentDay = currentDay.plusDays(1)
            }
            val pts = (0..6).map { Page(labels[it], it.toFloat(), week[it]) }
            pts to labels
        }

        // 1개월 20칸 등분
        DateRangePreset.MONTH -> {
            val part = 20
            val total = (ChronoUnit.DAYS.between(startDay, endDay) + 1).toInt()
            val pts = (0 until part).map {
                val startIndex = (it * total) / part
                val endIndex = ((it + 1) * total) / part - 1
                var sum = 0f
                for (dayIndex in startIndex..endIndex) {
                    val day = startDay.plusDays(dayIndex.toLong())
                    sum += byDate[day] ?: 0f
                }
                val label = "${startDay.plusDays(startIndex.toLong()).dayOfMonth}일"
                Page(label, it.toFloat(), sum)
            }
            pts to pts.map { it.label }
        }

        // 3개월 10일 단위
        DateRangePreset.THREE_MONTH -> {
            val windows = buildList {
                var end = endDay
                while (!end.isBefore(startDay)) {
                    val start = end.minusDays(9)
                    add(maxOf(startDay, start) to end)
                    end = start.minusDays(1)
                }
            }.asReversed()

            val pts = windows.mapIndexed { index, (start, end) ->
                var sum = 0f
                var date = start
                while (!date.isAfter(end)) {
                    sum += byDate[date] ?: 0f
                    date = date.plusDays(1)
                }
                Page("${end.dayOfMonth}일", index.toFloat(), sum)
            }
            pts to pts.map { it.label }
        }

        // 6개월 월별 합계
        DateRangePreset.SIX_MONTH -> {
            val months = buildList {
                var yearMonth = YearMonth.from(startDay)
                val last = YearMonth.from(endDay)
                while (!yearMonth.isAfter(last)) {
                    add(yearMonth)
                    yearMonth = yearMonth.plusMonths(1)
                }
            }
            val byMonth = byDate.entries
                .groupBy({ YearMonth.from(it.key) }, { it.value })
                .mapValues { it.value.sum() }

            val pts = months.mapIndexed { index, month ->
                Page("${month.monthValue}월", index.toFloat(), byMonth[month] ?: 0f)
            }
            pts to months.map { "${it.monthValue}월" }
        }

        // 1년 월별 합계
        DateRangePreset.YEAR -> {
            val months = buildList {
                var yearMonth = YearMonth.from(startDay)
                val last = YearMonth.from(endDay)
                while (!yearMonth.isAfter(last)) {
                    add(yearMonth); yearMonth = yearMonth.plusMonths(1)
                }
            }
            val byMonth = byDate.entries
                .groupBy({ YearMonth.from(it.key) }, { it.value })
                .mapValues { it.value.sum() }

            val pts = months.mapIndexed { index, month ->
                Page("${month.monthValue}월", index.toFloat(), byMonth[month] ?: 0f)
            }
            val labels = months.map { "${it.monthValue}월" }
            pts to labels
        }
    }
}
