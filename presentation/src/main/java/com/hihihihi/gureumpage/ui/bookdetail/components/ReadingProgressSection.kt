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
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import com.hihihihi.gureumpage.common.utils.formatDateToSimpleString
import com.hihihihi.gureumpage.designsystem.components.GureumLinearProgressBar
import com.hihihihi.gureumpage.designsystem.components.Medi12Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.bookdetail.mock.dummyUserBook
import java.util.Date

@Composable
fun ReadingProgressSection(
    userBook: UserBook
) {
    val start = formatDateToSimpleString(userBook.startDate)
    val end = if(userBook.status == ReadingStatus.FINISHED) formatDateToSimpleString(userBook.endDate) else ""

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
            userBook.currentPage.toFloat()/userBook.totalPage
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = buildAnnotatedString {
                    append(start)
                    append(" ~ ")
                    append(end)
                },
                style = GureumTypography.bodySmall,
                color = GureumTheme.colors.gray500,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = GureumTheme.colors.primaryDeep)) { append(userBook.currentPage.toString()) } // TODO: 값으로 주기
                    withStyle(SpanStyle(GureumTheme.colors.gray300)) { append("/${userBook.totalPage}") }
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
        ReadingProgressSection(dummyUserBook)
    }
}