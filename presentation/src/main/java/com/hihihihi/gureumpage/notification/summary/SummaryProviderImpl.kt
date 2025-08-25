package com.hihihihi.gureumpage.notification.summary

import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.DateRangePreset
import com.hihihihi.domain.usecase.statistics.GetStatisticsUseCase
import com.hihihihi.domain.usecase.statistics.presetToRange
import com.hihihihi.domain.usecase.userbook.GetUserBooksUseCase
import kotlinx.coroutines.flow.first
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class SummaryProviderImpl @Inject constructor(
    private val getStatistics: GetStatisticsUseCase,
    private val getUserBooks: GetUserBooksUseCase,
) : SummaryProvider {

    fun Int.toHourMin() = if (this < 60) "${this}분" else "${this / 60}시간 ${this % 60}분"
    private suspend fun build(preset: DateRangePreset): Pair<String, String> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return "요약" to "로그인이 필요해요."
        val stat = getStatistics(uid, preset).first()
        val range = presetToRange(preset)

        val totalPages = stat.pages.sumOf { it.y.toDouble() }.toInt()
        val totalMin = stat.time.sumOf { it.value.toDouble() }.toInt()
        val topTime = stat.time.maxByOrNull { it.value }?.label ?: "—"
        val topGenre = stat.category.maxByOrNull { it.value }?.label ?: "—"
        val activeDays = stat.pages.count { it.y > 0 }
        val monthsInRange = ChronoUnit.MONTHS.between(YearMonth.from(range.start), YearMonth.from(range.end)) + 1
        val booksFinished = getUserBooks(uid).first().size

        val year = range.end.year
        val month = range.end.monthValue

        val title = when (preset) {
            DateRangePreset.WEEK -> "구름이의 주간 요약 도착! ☁\uFE0F"
            DateRangePreset.MONTH -> "구름이의 월간 요약 도착! \uD83C\uDF19"
            DateRangePreset.YEAR -> "$year 리딩 어워즈 \uD83C\uDFC6"
            else -> ""
        }
        val avg = if (monthsInRange > 0) (totalPages / monthsInRange).toInt() else 0

        val body = if (totalPages == 0 && totalMin == 0) {
            "통계가 없어요. 오늘 한 장부터 시작해볼까요?"
        } else when (preset) {
            DateRangePreset.WEEK -> "지난주 동안 ${totalPages}쪽 · ${totalMin.toHourMin()}시간 읽었어요! 최고 시간대는 $topTime"
            DateRangePreset.MONTH -> "${month}월 총 ${totalPages}쪽 · ${totalMin.toHourMin()}시간 읽었어요! 완독한 책은 총 ${booksFinished}권!"
            DateRangePreset.YEAR -> "${year}년 총 ${booksFinished}권 읽었어요! · 최다 장르 $topGenre · 월 평균 ${avg}쪽"
            else -> ""
        }

        return title to body
    }

    override suspend fun weekly(): Pair<String, String> = build(DateRangePreset.WEEK)

    override suspend fun monthly(): Pair<String, String> = build(DateRangePreset.MONTH)

    override suspend fun yearly(): Pair<String, String> = build(DateRangePreset.YEAR)
}