package com.hihihihi.gureumpage.ui.statistics

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.TitleText
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.ui.statistics.components.GenreCard
import com.hihihihi.gureumpage.ui.statistics.components.ReadingPageCard
import com.hihihihi.gureumpage.ui.statistics.components.ReadingTimeCard

@Composable
fun StatisticsScreen() {
    val scrollState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        contentPadding = PaddingValues(vertical = 20.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            TitleText("독서 장르 분포")
            Spacer(modifier = Modifier.height(12.dp))
            GenreCard()
        }

        item {
            TitleText("독서 시간 분포")
            Spacer(modifier = Modifier.height(12.dp))
            ReadingTimeCard()
        }

        item {
            TitleText("주간 독서 페이지")
            Spacer(modifier = Modifier.height(12.dp))
            ReadingPageCard()
        }
    }
}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatisticsPreview() {
    GureumPageTheme {
        StatisticsScreen()
    }
}