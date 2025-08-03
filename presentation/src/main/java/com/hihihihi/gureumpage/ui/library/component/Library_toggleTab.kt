package com.hihihihi.gureumpage.ui.library.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun ToggleTab(
    isBeforeReading: Boolean,
    onToggle: (Boolean) -> Unit
) {
    //현재 선택된 탭 인덱스 결정
    val selectedTabIndex = if (isBeforeReading) 0 else 1
    val tabs = listOf("일기 전", "읽은 후")

    //탭 ui
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(GureumTheme.colors.card)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = GureumTheme.colors.card,
            contentColor = GureumTheme.colors.primary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = GureumTheme.colors.primary
                )
            },
        ) {
            tabs.forEachIndexed { index, title ->
                val selected = selectedTabIndex == index

                Tab(
                    selected = selected,
                    onClick = { onToggle(index == 0) },
                    selectedContentColor = GureumTheme.colors.primary,
                    unselectedContentColor = GureumTheme.colors.gray500,
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
        }
    }
}