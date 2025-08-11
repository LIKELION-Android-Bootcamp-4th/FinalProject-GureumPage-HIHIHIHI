package com.hihihihi.gureumpage.ui.bookdetail.components

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.Quote
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.bookdetail.components.tabs.BookInfoTab
import com.hihihihi.gureumpage.ui.bookdetail.components.tabs.QuotesTab
import com.hihihihi.gureumpage.ui.bookdetail.components.tabs.ReadingRecordTab
import com.hihihihi.gureumpage.ui.bookdetail.mock.dummyQuotes
import com.hihihihi.gureumpage.ui.bookdetail.mock.dummyRecords
import kotlinx.coroutines.launch

@Composable
fun BookDetailTabs(
    quotes: List<Quote>,
    histories: List<History>
) {
    val tabTitles = listOf("책 정보", "필사 목록", "독서 기록")
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalDivider(thickness = 4.dp, color = GureumTheme.colors.dividerShallow)

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = GureumTheme.colors.background,
            divider = { HorizontalDivider(thickness = 2.dp, color = GureumTheme.colors.dividerShallow) },
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) },
                    selectedContentColor = GureumTheme.colors.gray800,
                    unselectedContentColor = GureumTheme.colors.gray300,
                    modifier = Modifier.height(48.dp),
                    interactionSource = remember { MutableInteractionSource() },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            when (page) {
                0 -> BookInfoTab()
                1 -> QuotesTab(quotes)
                2 -> ReadingRecordTab(histories)
            }
        }
    }
}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookDetailTabsPreview() {
    GureumPageTheme {
        BookDetailTabs(dummyQuotes, dummyRecords)
    }
}