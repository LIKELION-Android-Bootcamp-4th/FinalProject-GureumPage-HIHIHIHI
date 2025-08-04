package com.hihihihi.gureumpage.ui.library.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun ToggleTab(
    isBeforeReading: Boolean,
    onToggle: (Boolean) -> Unit
) {
    //현재 선택된 탭 인덱스 결정(일기 전 0, 읽은 후 1)
    val selectedTabIndex = if (isBeforeReading) 0 else 1
    val tabs = listOf("읽기 전", "읽은 후")

    //탭 ui
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = GureumTheme.colors.card, // 바탕 색
        contentColor = GureumTheme.colors.primary,  // 텍스트 색
        indicator = {},
        divider = {},
        modifier = Modifier
            .padding(horizontal = 8.dp) // 양옆 여백
            .clip(RoundedCornerShape(20.dp)) // 전체 pill 형태
            .background(GureumTheme.colors.card) //카드 배경
            .padding(2.dp) // 내부 여백
    ) {
        tabs.forEachIndexed { index, text ->
            val selected = selectedTabIndex == index
            Tab(
                selected = selected,
                onClick = { onToggle(index == 0) }, //읽기 전을 true로 보냄
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp)) // 각 탭 pill 모양
                    .background(
                        if (selected) GureumTheme.colors.primary //선택된 탭 배경
                        else GureumTheme.colors.card //선택되지 않은 탭 배경
                    )
                    .padding(vertical = 0.dp), // 탭 높이 최소화
                text = {
                    Text(
                        text = text,
                        style = GureumTypography.titleMedium,
                        color = if (selected) GureumTheme.colors.white //선택된 탭 텍스트 색
                        else GureumTheme.colors.gray500 // 비선택 탭 텍스트 색
                    )
                }
            )
        }
    }
}