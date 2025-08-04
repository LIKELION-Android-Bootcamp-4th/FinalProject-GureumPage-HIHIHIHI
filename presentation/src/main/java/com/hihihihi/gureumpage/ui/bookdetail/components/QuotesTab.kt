package com.hihihihi.gureumpage.ui.bookdetail.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.ExpandableText
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun QuotesTab(/*아무튼 필사 목록*/) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // TODO 아무튼 필사 리스트 반복문

        QuoteCard("2025.07.29", 120, "네가 4시에 온다면 난 3시부터 행복할거야 네가 4시에 온다면 난 3시부터 행복할거야네가 4시에 온다면 난 3시부터 행복할거야네가 4시에 온다면 난 3시부터 행복할거야네가 4시에 온다면 난 3시부터 행복할거야네가 4시에 온다면 난 3시부터 행복할거야네가 4시에 온다면 난 3시부터 행복할거야네가 4시에 온다면 난 3시부터 행복할거야네가 4시에 온다면 난 3시부터 행복할거야")
    }
}

@Composable
private fun QuoteCard(
    date: String,
    page: Int?,
    quote: String,
) {
    GureumCard(
        modifier = Modifier.padding(top = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 14.dp, top = 8.dp)
                .padding()
        ) {
            Row(
                modifier = Modifier.padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    style = GureumTypography.bodySmall,
                    color = GureumTheme.colors.gray400,
                )
                Spacer(Modifier.width(12.dp))

                Text(
                    text = "${page}P",
                    style = GureumTypography.bodySmall,
                    color = GureumTheme.colors.gray400,
                )
                Spacer(Modifier.weight(1f))

                IconButton(
                    onClick = { /* TODO: 수정 삭제 팝업 메뉴 */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_horizontal_ellipsis_outline),
                        contentDescription = "dot_menu",
                        tint = GureumTheme.colors.gray600,
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }

            ExpandableText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                text = quote
            )
        }
    }
}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun QuotesTabPreview() {
    GureumPageTheme {
        QuotesTab()
    }
}