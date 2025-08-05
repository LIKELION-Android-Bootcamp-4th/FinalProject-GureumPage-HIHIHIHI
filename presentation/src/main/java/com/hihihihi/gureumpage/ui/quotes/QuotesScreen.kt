package com.hihihihi.gureumpage.ui.quotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.BodySubText
import com.hihihihi.gureumpage.designsystem.theme.GureumColors
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.quotes.component.QuoteItem
import com.hihihihi.gureumpage.ui.quotes.model.Quote
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen() {
    val quotesList = listOf<Quote>(
        Quote(
            "책 제목 1",
            "2025.08.04",
            "https://image.yes24.com/goods/141792177/XL",
            "필사 내용입니다.\n필사 내용입니다.\n필사 내용입니다.\n필사 내용입니다.\n필사 내용입니다.\n필사 내용입니다.",
            1,
            "저자1",
            "출판사1"
        ),
        Quote(
            "책 제목 2",
            "2025.08.05",
            "https://image.yes24.com/goods/141792177/XL",
            "필사 내용입니다.",
            2,
            "저자2",
            "출판사3"
        ),
    )
    var sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedQuote by remember { mutableStateOf<Quote?>(null) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GureumColors.defaultLightColors().background10)
        ) {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(56.dp), title = {
                    Text(
                        text = "필사 목록",
                        style = MaterialTheme.typography.titleLarge,
                        color = GureumTheme.colors.gray800
                    )
                })

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp),
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
                                        style = GureumTypography.bodyLarge,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row {
                                        //날짜
                                        BodySubText(selectedQuote!!.date)
                                        Spacer(modifier = Modifier.padding(start = 10.dp))
                                        //페이지
                                        BodySubText(selectedQuote!!.page.toString() + 'p')
                                    }
                                    //저자
                                    BodySubText(selectedQuote!!.author)
                                    //출판사
                                    BodySubText(selectedQuote!!.publisher)
                                }

                                //닫기 버튼
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            sheetState.hide()
                                            selectedQuote = null
                                        }
                                    }) {
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GureumColors.defaultLightColors().bookBackground)
                    ) {
                        Text(
                            text = selectedQuote!!.content,
                            style = GureumTypography.bodySmall,
                            color = GureumTheme.colors.gray800,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
