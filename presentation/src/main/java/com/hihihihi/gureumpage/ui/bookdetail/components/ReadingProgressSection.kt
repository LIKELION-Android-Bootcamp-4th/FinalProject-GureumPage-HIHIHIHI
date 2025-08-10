package com.hihihihi.gureumpage.ui.bookdetail.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.GureumLinearProgressBar
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import java.util.Date

@Composable
fun ReadingProgressSection(
//    startDate: Date,
//    progress: Double,
//    currentPage: Int,
//    totalPage: Int,
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "독서 진행도",
            style = GureumTypography.titleMedium,
            color = GureumTheme.colors.gray800,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        GureumLinearProgressBar(
            12,
            123.toFloat()/456
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("2025.06.24~", color = GureumTheme.colors.gray300)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = buildAnnotatedString {
//                    withStyle(SpanStyle(color = GureumTheme.colors.primaryDeep)) { append("$currentPage") } // TODO: 값으로 주기
//                    withStyle(SpanStyle(GureumTheme.colors.gray300)) { append("/$totalPage") }
                    withStyle(SpanStyle(color = GureumTheme.colors.primaryDeep)) { append("123") }
                    withStyle(SpanStyle(GureumTheme.colors.gray300)) { append("/456") }
                },
                style = GureumTypography.bodySmall,
            )
        }
    }
}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReadingProgressSectionPreview() {
    GureumPageTheme {
        ReadingProgressSection()
    }
}