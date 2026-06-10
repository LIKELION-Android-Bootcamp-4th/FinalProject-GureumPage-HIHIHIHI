package com.hihihihi.presentation.ui.mindmap.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import io.github.hanhyo.composemindmap.model.MindMapNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeDetailBottomSheet(
    node: MindMapNode,
    onDismiss: () -> Unit,
) {
    val colors = GureumTheme.colors
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val icon = node.icon
                if (icon != null) {
                    Text(icon, fontSize = 24.sp)
                }
                Text(
                    text = node.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.gray900,
                )
            }

            if (node.subtitle.isNotBlank()) {
                HorizontalDivider(color = colors.dividerShallow)
                Text(
                    text = node.subtitle,
                    fontSize = 14.sp,
                    color = colors.gray600,
                    lineHeight = 22.sp,
                )
            }
        }
    }
}
