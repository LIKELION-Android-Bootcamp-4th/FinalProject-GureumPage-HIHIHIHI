package com.hihihihi.gureumpage.ui.timer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun TimerControlsRow(
    isRunning: Boolean,
    onStop: () -> Unit,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
) {
    val colors = GureumTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(top = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽: 정지 (아이콘만)
        IconButton(
            onClick = onStop,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_stop),
                contentDescription = "정지",
                tint = colors.gray500,
                modifier = Modifier.size(40.dp)
            )
        }

        // 가운데: 재생/일시정지
        IconButton(
            onClick = onToggle,
            modifier = Modifier.size(72.dp) // 중앙은 터치 영역 살짝 크게
        ) {
            Icon(
                painter = painterResource(
                    if (isRunning) R.drawable.ic_pause else R.drawable.ic_play
                ),
                contentDescription = if (isRunning) "일시정지" else "재생",
                tint = colors.point,
                modifier = Modifier.size(72.dp)
            )
        }

        // 오른쪽: 메모/편집 (아이콘만)
        IconButton(
            onClick = onEdit,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_edit_alt_filled),
                contentDescription = "메모 작성",
                tint = colors.gray400,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}