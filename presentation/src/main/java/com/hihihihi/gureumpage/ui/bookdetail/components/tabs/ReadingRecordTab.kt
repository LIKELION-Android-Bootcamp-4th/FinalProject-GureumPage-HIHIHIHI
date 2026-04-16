package com.hihihihi.gureumpage.ui.bookdetail.components.tabs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.RecordType
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.common.utils.formatDateToSimpleString
import com.hihihihi.gureumpage.common.utils.formatSecondsToReadableTime
import com.hihihihi.gureumpage.common.utils.formatTimeRange
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.Medi14Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.bookdetail.mock.dummyRecords

@Composable
fun ReadingRecordTab(histories: List<History>) {
    if (histories.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Semi16Text(
                "아직 독서 기록이 없어요.",
                color = GureumTheme.colors.gray500
            )
            Spacer(Modifier.height(16.dp))
            Medi14Text(
                "독서 스톱워치로 독서를 시작하거나\n놓친 기록을 직접 추가해보세요!",
                color = GureumTheme.colors.gray400,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            histories
                .filter { it.date != null }
                .sortedByDescending { it.date }
                .groupBy { it.date!!.toLocalDate() }
                .forEach { (localDate, dailyRecords) ->
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = formatDateToSimpleString(dailyRecords.first().date),
                            modifier = Modifier,
                            color = GureumTheme.colors.gray400,
                            style = GureumTypography.bodySmall
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Semi16Text(
                            text = formatSecondsToReadableTime(
                                dailyRecords.sumOf { it.readTime }
                            ),
                            isUnderline = true
                        )
                    }
                    dailyRecords.forEach { record ->
                        RecordCard(
                            isTimer = record.recordType == RecordType.TIMER,
                            id = record.id,
                            timeRange = formatTimeRange(record.startTime, record.endTime),
                            duration = formatSecondsToReadableTime(record.readTime)
                        )
                    }
                }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        histories
            .filter { it.date != null }
            .sortedByDescending { it.date }
            .groupBy { it.date!!.toLocalDate() }
            .forEach { (localDate, dailyRecords) ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = formatDateToSimpleString(dailyRecords.first().date),
                        modifier = Modifier,
                        color = GureumTheme.colors.gray400,
                        style = GureumTypography.bodySmall
                    )
                    Spacer(modifier = Modifier.weight(1f))


                    Semi16Text(
                        text = formatSecondsToReadableTime(
                            dailyRecords.sumOf { it.readTime }
                        ),
                        isUnderline = true
                    )

//                    TitleText(
//                        text = formatSecondsToReadableTime(
//                            dailyRecords.sumOf { it.readTime }
//                        ),
//                        isUnderline = true
//                    )

                }
                dailyRecords.forEach { record ->
                    RecordCard(
                        isTimer = record.recordType == RecordType.TIMER,
                        id = record.id,
                        timeRange = formatTimeRange(record.startTime, record.endTime),
                        duration = formatSecondsToReadableTime(record.readTime)
                    )
                }
            }
    }
}

@Composable
private fun RecordCard(
    modifier: Modifier = Modifier,
    isTimer: Boolean = true,
    id: String,
    timeRange: String,
    duration: String
) {
    GureumCard(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(
                    if (isTimer) R.drawable.ic_alarm_filled
                    else R.drawable.ic_edit_filled
                ),
                contentDescription = "기록 형태",
                tint = GureumTheme.colors.gray300
            )
            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = timeRange,
                style = GureumTypography.titleMedium,
                color = GureumTheme.colors.gray600
            )
            Spacer(modifier = Modifier.weight(1f))
            Semi16Text(duration, color = GureumTheme.colors.gray800)
        }
    }
}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReadingRecordTabPreview() {
    GureumPageTheme {
        ReadingRecordTab(histories = dummyRecords)
    }
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RecordCardPreview() {
    GureumPageTheme {
        RecordCard(
            id = "0",
            isTimer = false,
            timeRange = "15:00 ~ 15:10",
            duration = "9분 30초"
        )
    }
}
