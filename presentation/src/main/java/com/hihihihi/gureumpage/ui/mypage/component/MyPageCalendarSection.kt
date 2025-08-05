package com.hihihihi.gureumpage.ui.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun MyPageCalenderSection() {
    val typography = GureumTypography
    val colors = GureumTheme.colors

    //TODO: 잔디 캘린더로 교체
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp)
            .background(colors.white, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("잔디 캘린더", style = typography.bodyMedium, color = colors.gray500)
    }
}