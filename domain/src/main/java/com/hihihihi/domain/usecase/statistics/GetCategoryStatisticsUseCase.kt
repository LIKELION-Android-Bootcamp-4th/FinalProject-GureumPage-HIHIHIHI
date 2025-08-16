package com.hihihihi.domain.usecase.statistics

import android.util.Log.e
import android.util.Log.i
import com.hihihihi.domain.model.CategorySlice
import com.hihihihi.domain.model.DailyReadPage
import com.hihihihi.domain.model.DateRange
import com.hihihihi.domain.model.DateRangePreset
import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.Page
import com.hihihihi.domain.model.Statistics
import com.hihihihi.domain.model.TimeSlice
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.DailyReadPageRepository
import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.repository.UserBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.DayOfWeek
import java.time.LocalDate
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
                    categoryFromUserBooks(books, range).also { e("STAT", "category 계산 완료: $it") }
                } catch (ex: Exception) {
                    e("STAT", "category 계산 에러", ex)
                    emptyList<CategorySlice>()
                }

                // 독서 시간 계산
                val time = try {
                    readingTime(histories, range).also { e("STAT", "readingTime 계산 완료: $it") }
                } catch (ex: Exception) {
                    e("STAT", "readingTime 에러", ex)
                    emptyList<TimeSlice>()
                }

                // 페이지 계산
                val (pages, xLabels) = try {
                    readingPages(dailies, preset, range).also { e("STAT", "readingPages 계산 완료: $it") }
                } catch (ex: Exception) {
                    e("STAT", "readingPages 에러", ex)
                    emptyList<Page>() to emptyList<String>()
                }

                // 최종 통계
                e("STAT", "Statistics 생성 완료: category=$category, time=$time, pages=$pages, xLabels=$xLabels")
                Statistics(category, time, pages, xLabels)
            } catch (e: Exception) {
                e("STAT", "combine 전체 에러:", e)
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
    return books.filter(::inRange)
        .groupBy { it.category ?: "기타" } // 카테고리 이름으로 그룹
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
        val s0 = hist.startTime ?: return@forEach
        val e0 = hist.endTime ?: return@forEach
        var s = if (s0.isBefore(range.start)) range.start else s0
        val e = if (e0.isAfter(range.end)) range.end else e0

        if (!s.isBefore(e)) return@forEach

        while (!s.toLocalDate().isAfter(e.toLocalDate())) {
            val day = s.toLocalDate()
            val segEnd = minOf(e, day.atTime(LocalTime.MAX))

            buckets.forEachIndexed { index, bucket ->
                val bs = day.atTime(bucket.start)
                val be =
                    if (bucket.end == LocalTime.MIDNIGHT) day.plusDays(1).atStartOfDay() else day.atTime(bucket.end)
                val startMax = maxOf(s, bs)
                val endMin = minOf(segEnd, be)

                if (startMax.isBefore(endMin)) {
                    val minutes = ChronoUnit.MINUTES.between(startMax, endMin)
                    acc[index] += minutes
                    e("STAT", "history=${hist.userBookId}, bucket=${bucket.label}, minutes=$minutes")
                }
            }

            // 안전하게 하루 단위로 이동
            s = day.plusDays(1).atStartOfDay()
            if (!s.isBefore(e) && s != e) break // 혹시 무한루프 방지
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
    val byDate = dailies.filter { readPage ->
        val dt = readPage.date.atStartOfDay()
        !dt.isBefore(range.start) && !dt.isAfter(range.end)
    }
        .groupBy { it.date }
        .mapValues { (_, daily) -> daily.sumOf { it.totalReadPageCount }.toFloat() }

    e("STAT", "filtered dailies: $byDate")

    fun label(date: LocalDate) = when (date.dayOfWeek) {
        DayOfWeek.SUNDAY -> "일"
        DayOfWeek.MONDAY -> "월"
        DayOfWeek.TUESDAY -> "화"
        DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY -> "목"
        DayOfWeek.FRIDAY -> "금"
        DayOfWeek.SATURDAY -> "토"
    }

    val startDay = range.start.toLocalDate()
    val endDay = range.end.toLocalDate()

    return when (preset) {
        DateRangePreset.WEEK, DateRangePreset.MONTH -> {
            val days = generateSequence(startDay) { it.plusDays(1) }.takeWhile { !it.isAfter(endDay) }.toList()
            val pts = days.mapIndexed { index, date ->
                val count = byDate[date] ?: 0f
                e("STAT", "date=$date, pages=$count")
                Page(
                    if (preset == DateRangePreset.WEEK) label(date) else date.dayOfMonth.toString(),
                    index.toFloat(),
                    count
                )
            }
            pts to days.map { if (preset == DateRangePreset.WEEK) label(it) else it.dayOfMonth.toString() }
        }

        DateRangePreset.THREE_MONTH, DateRangePreset.SIX_MONTH, DateRangePreset.YEAR -> {
            val months = buildList {
                var ym = YearMonth.from(startDay)
                val last = YearMonth.from(endDay)
                while (!ym.isAfter(last)) {
                    add(ym)
                    ym = ym.plusMonths(1)
                }
            }
            val byMonth = byDate.entries.groupBy({ YearMonth.from(it.key) }, { it.value })
                .mapValues { it.value.sum() }

            val pts = months.mapIndexed { index, month ->
                val count = byMonth[month] ?: 0f
                e("STAT", "month=$month, pages=$count")
                Page("${month.monthValue}월", index.toFloat(), count)
            }
            pts to months.map { it.monthValue.toString() }
        }
    }
}
