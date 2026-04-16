package com.hihihihi.gureumpage.ui.bookdetail.components.tabs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.UserBook
import com.hihihihi.gureumpage.designsystem.components.GureumGenreChip
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.bookdetail.mock.dummyUserBook

@Composable
fun BookInfoTab(
    userBook: UserBook
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
    ) {
        InfoSection("출판사") { InfoBody(userBook.publisher ?: "") }
        InfoSection("ISBN") { userBook.isbn13?.let { InfoBody(it) } }
        InfoSection("카테고리") { InfoGenre(userBook.category) }
        InfoSection("책 소개") { InfoBody(userBook.description ?: "") }
        InfoSection("총 페이지") { InfoBody(userBook.totalPage.toString()) }
        InfoSection("도서 DB 제공") { InfoBody("알라딘") }
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
private fun InfoGenre(category: String?) {
    if (category != null) {
        GureumGenreChip(category, modifier = Modifier.padding(bottom = 18.dp))
    }
}


@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookInfoTabPreview() {
    GureumPageTheme {
        BookInfoTab(dummyUserBook)
    }
}