package com.hihihihi.gureumpage.ui.timer

data class TimerUiState(
    val bookTitle: String = "",
    val author: String = "",
    val bookImageUrl: String = "",
    val elapsedSec: Int = 0, // 스톱워치 사용 x
    val targetSec: Int = 60 * 30,
    val isRunning: Boolean = false,
    val memoLines: List<String> = emptyList(),
    //30분 마다 프로그래스바 갱신
    val ringPeriodSec: Int = 1800,
    val startPage: Int? = null,
    val totalPage: Int? = null,
    val countdown: Int? = null
) {
    val progress: Float
        get() {
            val period = ringPeriodSec.coerceAtLeast(1)
            return ((elapsedSec % period).toFloat() / period).coerceIn(0f, 1f)
        }

    val displayTimeMMSS: String
        get() {
            val h = elapsedSec / 3600
            val m = (elapsedSec % 3600) / 60
            val s = elapsedSec % 60
            return if (h > 0) "%d:%02d:%02d".format(h, m, s)
            else "%02d:%02d".format(m, s)
        }
}