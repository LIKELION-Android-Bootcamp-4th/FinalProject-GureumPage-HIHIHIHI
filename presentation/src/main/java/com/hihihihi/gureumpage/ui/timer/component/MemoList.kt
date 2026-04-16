package com.hihihihi.gureumpage.ui.timer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun MemoList(
    lines: List<String>,
    modifier: Modifier = Modifier,
    itemSpacing: androidx.compose.ui.unit.Dp = 12.dp
) {
    val colors = GureumTheme.colors

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        itemsIndexed(lines, key = { index, _ -> index }) { _, text ->
            Text(
                text = text,
                style = GureumTypography.bodyMedium,
                color = colors.gray300
            )
        }
    }
}