package com.hihihihi.gureumpage.ui.bookdetail.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.GureumGenreChip
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun BookInfoTab() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
    ) {
        InfoSection("출판사") { InfoBody("출판사입니다.") }
        InfoSection("ISBN") { InfoBody("K962037084 9791192372730") }
        InfoSection("장르") { InfoGenre() }
        InfoSection("책 소개") { InfoBody("이 책은 독일에서부터 시작되어.. 이 책을 받고 다른 사람에게 돌리지 않을 경우 당신은 행복해지고 삶이 어쩌고 저쩌고") }
        InfoSection("총 페이지") { InfoBody("260") }
    }
}

@Composable
private fun InfoSection(title: String, content: @Composable () -> Unit) {
    InfoTitle(title)
    content()
}

@Composable
private fun InfoTitle(text: String) {
    Text(
        text = text,
        style = GureumTypography.bodySmall,
        color = GureumTheme.colors.gray800,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun InfoBody(text: String) {
    Text(
        text = text,
        style = GureumTypography.bodyMedium,
        color = GureumTheme.colors.gray400,
        modifier = Modifier.padding(bottom = 20.dp),
    )
}

@Composable
private fun InfoGenre() {
    GureumGenreChip("로맨스", modifier = Modifier.padding(bottom = 18.dp))
}


@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookInfoTabPreview() {
    GureumPageTheme {
        BookInfoTab()
    }
}