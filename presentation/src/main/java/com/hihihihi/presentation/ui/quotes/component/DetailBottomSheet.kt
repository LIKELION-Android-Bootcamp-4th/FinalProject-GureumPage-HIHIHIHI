package com.hihihihi.presentation.ui.quotes.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hihihihi.presentation.designsystem.components.BodySubText
import com.hihihihi.presentation.designsystem.components.BookCoverImage
import com.hihihihi.presentation.designsystem.components.Medi16Text
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.designsystem.theme.GureumTypography
import com.hihihihi.presentation.ui.model.QuoteUiModel
import com.hihihihi.presentation.utils.formatDateToSimpleString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBottomSheet(
    quote: QuoteUiModel,
    sheetState: SheetState,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = GureumTheme.colors.card,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                // 책 표지
                BookCoverImage(
                    modifier = Modifier
                        .size(width = 60.dp, height = 80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    imageUrl = quote.imageUrl,
                )
                Column(modifier = Modifier.padding(start = 15.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column {
                            // 책 제목
                            Text(
                                text = quote.title,
                                maxLines = 1,
                                style = GureumTypography.bodyLarge,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Row {
                                // 날짜
                                BodySubText(formatDateToSimpleString(quote.createdAt))
                                Spacer(modifier = Modifier.padding(start = 10.dp))
                                // 페이지
                                quote.pageNumber?.let { page ->
                                    BodySubText("${page}p")
                                }
                            }
                            // 저자
                            BodySubText(quote.author)
                            // 출판사
                            BodySubText(quote.publisher)
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            // 필사 내용
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GureumTheme.colors.bookBackground),
            ) {
                Medi16Text(
                    text = quote.content,
                    color = GureumTheme.colors.gray800,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}
