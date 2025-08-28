package com.hihihihi.gureumpage.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.Medi14Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

// 에러상태 뷰
@Composable
fun ErrorView(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Semi16Text(
            text = message,
            color = GureumTheme.colors.gray600,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Medi14Text(
            text = "잠시 후 다시 시도해주세요",
            color = GureumTheme.colors.gray500,
            textAlign = TextAlign.Center
        )
    }
}
