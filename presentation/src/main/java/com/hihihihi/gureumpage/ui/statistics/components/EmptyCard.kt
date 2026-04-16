package com.hihihihi.gureumpage.ui.statistics.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.Medi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi18Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme

@Composable
fun EmptyCard(
    mainText: String = "아직 통계가 없어요",
    subText: String = "첫 책을 추가해 통계를 시작해 보세요.",
) {
    GureumCard(modifier = Modifier.height(210.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Semi18Text(mainText)
            Spacer(Modifier.height(16.dp))
            Medi16Text(subText)
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyCardPreview() {
    GureumPageTheme {
        EmptyCard()
    }
}