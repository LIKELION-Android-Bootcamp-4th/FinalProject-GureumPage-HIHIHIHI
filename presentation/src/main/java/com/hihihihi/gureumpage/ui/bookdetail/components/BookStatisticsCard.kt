package com.hihihihi.gureumpage.ui.bookdetail.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.bookdetail.BookStatistic

@Composable
fun BookStatisticsCard(
    bookStatistic: BookStatistic
) {
    GureumCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 30.dp)
            .padding(bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalCenterText("독서 기간", bookStatistic.readingPeriod)
            VerticalDivider(
                modifier = Modifier.height(40.dp),
                color = GureumTheme.colors.gray200
            )
            HorizontalCenterText("누적 독서", bookStatistic.totalReadingTime)
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(1f),
                color = GureumTheme.colors.gray200
            )
            HorizontalCenterText("하루 평균", bookStatistic.averageDailyTime)
        }
    }
}

@Composable
private fun HorizontalCenterText(title: String, value: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            style = GureumTypography.bodySmall,
            color = GureumTheme.colors.gray800
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = GureumTypography.titleMedium,
            color = GureumTheme.colors.gray800
        )
    }
}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookStatisticsCardPreview() {
    GureumPageTheme {
        BookStatisticsCard(BookStatistic("","",""))
    }
}