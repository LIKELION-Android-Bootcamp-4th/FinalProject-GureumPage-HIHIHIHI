package com.hihihihi.gureumpage.ui.quotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumColors
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.quotes.component.QuoteItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen() {
    val quotesList = listOf<Quote>(
        Quote(
            "title1",
            "2025.08.04",
            "https://image.yes24.com/goods/141792177/XL",
            "content2 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            1
        ),
        Quote(
            "title2",
            "2025.08.05",
            "https://image.yes24.com/goods/141792177/XL",
            "content2 a",
            12
        ),
        Quote(
            "title3",
            "2025.08.06",
            "https://image.yes24.com/goods/141792177/XL",
            "content3 b",
            123
        ),
        Quote(
            "title4",
            "2025.08.07",
            "https://image.yes24.com/goods/141792177/XL",
            "content4 c",
            2
        ),
        Quote(
            "title1",
            "2025.08.08",
            "https://image.yes24.com/goods/141792177/XL",
            "content1 ddddddddddd",
            22
        ),
        Quote(
            "title5",
            "2025.08.09",
            "https://image.yes24.com/goods/141792177/XL",
            "content5 ee",
            222
        ),
        Quote(
            "title6",
            "2025.08.10",
            "https://image.yes24.com/goods/141792177/XL",
            "content6 ffffff",
            33333333
        ),

        )
    var sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedQuote by remember { mutableStateOf<Quote?>(null) }



    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "필사 목록",
                        style = MaterialTheme.typography.titleLarge,
                        color = GureumTheme.colors.gray800
                    )
                })

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(quotesList) { item ->
                    QuoteItem(
                        item = item, onItemClick = {
                            selectedQuote = item
                            scope.launch {
                                sheetState.show()
                            }
                        })
                }
            }
        }

        //모달 시트
        if (selectedQuote != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        selectedQuote = null
                    }
                },
                containerColor = GureumTheme.colors.background,
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        //책 표지
                        AsyncImage(
                            modifier = Modifier
                                .size(width = 60.dp, height = 80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                            model = selectedQuote!!.image,
                            contentDescription = "책 표지",
                        )
                        Column(modifier = Modifier.padding(start = 15.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    //책 제목
                                    Text(
                                        text = selectedQuote!!.title,
                                        maxLines = 1,
                                        style = GureumTypography.bodyMedium,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row {
                                        //날짜
                                        Text(
                                            text = selectedQuote!!.date,
                                            maxLines = 1,
                                            style = GureumTypography.bodySmall,
                                            color = GureumColors.defaultLightColors().gray400,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        //페이지
                                        Text(
                                            modifier = Modifier.padding(start = 10.dp),
                                            text = selectedQuote!!.page.toString() + 'p',
                                            maxLines = 1,
                                            style = GureumTypography.bodySmall,
                                            color = GureumColors.defaultLightColors().gray400,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                //닫기 버튼
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            sheetState.hide()
                                            selectedQuote = null
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_close),
                                        contentDescription = "닫기",
                                        tint = GureumColors.defaultLightColors().gray500
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                    //필사 내용
                    Text(
                        text = selectedQuote!!.content,
                        style = GureumTypography.bodySmall,
                        color = GureumColors.defaultLightColors().gray800,
                    )
                }
            }
        }
    }
}

data class Quote(
    val title: String, val date: String, val image: String, val content: String, val page: Int
)
