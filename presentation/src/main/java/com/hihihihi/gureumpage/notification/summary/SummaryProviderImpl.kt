package com.hihihihi.gureumpage.notification.summary

import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.DateRangePreset
import com.hihihihi.domain.usecase.statistics.GetStatisticsUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SummaryProviderImpl @Inject constructor(
    private val getStatistics: GetStatisticsUseCase
) : SummaryProvider {

    private fun Int.toMin() = if (this <= 0) "0분" else "${this}분"

    private suspend fun build(preset: DateRangePreset): Pair<String, String> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return "요약" to "로그인이 필요해요."
        val stat = getStatistics(uid, preset).first()

        val totalPages = stat.pages.sumOf { it.y.toDouble() }.toInt()
        val totalMin = stat.time.sumOf { it.value.toDouble() }.toInt()
        val topTime = stat.time.maxByOrNull { it.value }?.label ?: "—"
        val topGenre = stat.category.maxByOrNull { it.value }?.label ?: "—"

        val (title, lead) = when (preset) {
            DateRangePreset.WEEK -> "주간 요약" to "지난주"
            DateRangePreset.MONTH -> "월간 요약" to "지난달"
            DateRangePreset.YEAR -> "연간 리캡" to "올해"
            else -> "요약" to ""
        }

        val body = if (totalPages == 0 && totalMin == 0) {
            "$lead 통계가 없어요. 오늘 한 장부터 시작해볼까요?"
        } else {
            // TODO 각 프리셋 별 구분할지
            "$lead ${totalPages}쪽 · ${totalMin.toMin()}\n최고 시간대: $topTime · 최다 장르: $topGenre"
        }

        return title to body
    }

    override suspend fun weekly(): Pair<String, String> = build(DateRangePreset.WEEK)

    override suspend fun monthly(): Pair<String, String> = build(DateRangePreset.MONTH)

    override suspend fun yearly(): Pair<String, String> = build(DateRangePreset.YEAR)
}