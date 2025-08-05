package com.hihihihi.gureumpage.ui.quotes.component

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hihihihi.domain.model.Quote
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.BodySubText
import com.hihihihi.gureumpage.designsystem.theme.GureumColors
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBottomSheet(
    quote: Quote,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = GureumTheme.colors.background,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 책 표지
                AsyncImage(
                    modifier = Modifier
                        .size(width = 60.dp, height = 80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    model = quote.imageUrl,
                    contentDescription = "책 표지",
                )
                Column(modifier = Modifier.padding(start = 15.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            // 책 제목
                            Text(
                                text = quote.title,
                                maxLines = 1,
                                style = GureumTypography.bodyLarge,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row {
                                // 날짜
                                BodySubText(quote.createdAt.toString().split("T")[0])
                                Spacer(modifier = Modifier.padding(start = 10.dp))
                                // 페이지
                                BodySubText(quote.pageNumber.toString() + 'p')
                            }
                            // 저자
                            BodySubText(quote.author)
                            // 출판사
                            BodySubText(quote.publisher)
                        }

                        // 닫기 버튼
                        IconButton(onClick = { onDismiss() }) {
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
            // 필사 내용
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GureumColors.defaultLightColors().bookBackground)
            ) {
                Text(
                    text = quote.content,
                    style = GureumTypography.bodySmall,
                    color = GureumTheme.colors.gray800,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
