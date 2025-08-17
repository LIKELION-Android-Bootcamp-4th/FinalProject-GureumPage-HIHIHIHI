package com.hihihihi.gureumpage.ui.timer.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.BodySubText
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun MemoList(
    lines: List<String>,
    modifier: Modifier = Modifier,
    itemSpacing: androidx.compose.ui.unit.Dp = 12.dp
) {
    val colors = GureumTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        lines.forEachIndexed { idx, text ->
            Text(
                text = text,
                style = GureumTypography.bodyMedium,
                color = colors.gray300
            )
            if (idx < lines.lastIndex) {
                Spacer(Modifier.height(itemSpacing))
            }
        }
    }
}