package com.hihihihi.gureumpage.ui.quotes.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hihihihi.domain.model.Quote
import com.hihihihi.gureumpage.common.utils.formatDateToSimpleString
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun QuoteItem(item: Quote, onItemClick: (Quote) -> Unit) {
    GureumCard(
        corner = 16.dp,
        onClick = { onItemClick(item) }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            //책 이미지
            AsyncImage(
                modifier = Modifier
                    .size(width = 60.dp, height = 80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                model = item.imageUrl,
                contentDescription = "책",
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                //책 제목
                Text(
                    text = item.title,
                    style = GureumTypography.bodyLarge,
                    color = GureumTheme.colors.gray600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                //날짜
                Text(
                    text = formatDateToSimpleString(item.createdAt),
                    style = GureumTypography.bodyMedium,
                    color = GureumTheme.colors.gray300,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                //작성한 필사
                Text(
                    text = item.content,
                    style = GureumTypography.bodyMedium,
                    color = GureumTheme.colors.gray800,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }
    }
}
