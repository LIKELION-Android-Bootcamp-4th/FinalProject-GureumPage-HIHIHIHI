package com.hihihihi.gureumpage.ui.timer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hihihihi.gureumpage.designsystem.components.BodySubText
import com.hihihihi.gureumpage.designsystem.components.BodyText
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun NowReadingCard(
    title: String,
    author: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = GureumTheme.colors
    val isDark = GureumTheme.isDarkTheme

    GureumCard(
        modifier = modifier,
        corner = 12.dp,
        elevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // === 표지 ===
            if (imageUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.dividerShallow)
                )
            } else {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "책 표지",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(Modifier.width(12.dp))

            // === 텍스트 ===
            Column(Modifier.weight(1f)) {
                BodyText(
                    text = title,
                    maxLine = 1,
                    color = if (isDark) colors.white else colors.gray900, // 다크 배경에서 더 잘 보이게
                    style = GureumTypography.titleMedium
                )
                Spacer(Modifier.height(2.dp))
                BodySubText(
                    text = author,
                    maxLine = 1,
                    color = if (isDark) colors.gray300 else colors.gray500,
                    style = GureumTypography.bodySmall
                )
            }
        }
    }
}