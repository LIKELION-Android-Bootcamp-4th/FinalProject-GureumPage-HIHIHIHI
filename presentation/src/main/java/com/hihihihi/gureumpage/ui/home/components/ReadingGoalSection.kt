package com.hihihihi.gureumpage.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.common.utils.formatSecondsToReadableTimeWithoutSecond
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.GureumCircleProgressBar
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun ReadingGoalSection(
    totalReadSeconds: Int,
    goalSeconds: Int,
    onGoalChange: (Int) -> Unit
) {
    val isGoalSet = goalSeconds > 0
    val progress = if (isGoalSet) (totalReadSeconds.toFloat() / goalSeconds).coerceIn(0f, 1f) else 0f

    val goalMinutes = goalSeconds / 60
    val goalHours = goalMinutes / 60
    val goalRemainingMinutes = goalMinutes % 60

    var isPickerOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(GureumTheme.colors.background)
            .padding(16.dp)
    ) {
        Semi16Text("오늘 목표 독서 달성", isUnderline = true)
        Spacer(Modifier.height(12.dp))
        GureumCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,  // Bottom에서 CenterVertically로 변경
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { isPickerOpen = true }) {
                    GureumCircleProgressBar(
                        strokeWidth = 12,
                        size = 140,
                        progress = progress
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        if (isGoalSet) {
                            Text(
                                text = formatSecondsToReadableTimeWithoutSecond(totalReadSeconds),
                                style = GureumTypography.headlineLarge,
                                color = if (totalReadSeconds > goalSeconds) GureumTheme.colors.primary else GureumTheme.colors.gray900
                            )
                            Row(
                                // 가운데가 잘 안맞아서.. 호호
                                modifier = Modifier.padding(start = 12.dp),
                            ) {
                                Text(
                                    text = if (isGoalSet) {
                                        buildString {
                                            if (goalHours > 0) append("${goalHours}시간 ")
                                            if (goalRemainingMinutes > 0) append("${goalRemainingMinutes}분")
                                        }
                                    } else {
                                        "00분"
                                    },
                                    style = GureumTypography.bodyMedium,
                                    color = GureumTheme.colors.gray500
                                )
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_down),
                                    contentDescription = "목표 시간 설정",
                                    tint = GureumTheme.colors.gray500,
                                    modifier = Modifier
                                        .size(16.dp)
                                )
                            }

                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_plus),
                                contentDescription = "목표 시간 설정",
                                tint = Color.Gray,
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.width(20.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (!isGoalSet) {
                        // 목표가 없을 때
                        Text(
                            "목표를 설정해주세요!",
                            style = GureumTypography.bodyMedium,
                            color = Color.Gray
                        )

                    } else {
                        // 목표 설정된 경우
                        val imageResId = when {
                            //TODO 이미지들은 한번 더 손 봐야함
                            progress < 0.25f -> R.drawable.bg_book_25
                            progress < 0.5f -> R.drawable.bg_book_25
                            progress < 0.75f -> R.drawable.bg_book_50
                            progress < 0.99f -> R.drawable.bg_book_75
                            else -> R.drawable.bg_book_75
                        }

                        if (progress > 0) {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = "진행 이미지",
                                modifier = Modifier.size(100.dp)
                            )
                        } else {
                            Text(
                                "오늘의 독서를 시작해주세요!",
                                style = GureumTypography.bodyMedium,
                                color = GureumTheme.colors.gray500,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

    }

    if (isPickerOpen) {
        ReadingGoalPicker(
            initialHour = goalSeconds / 3600,
            initialMinute = (goalSeconds % 3600) / 60,
            onDismiss = { isPickerOpen = false },
            onConfirm = { hour, minute ->
                onGoalChange(hour * 3600 + minute * 60)
                isPickerOpen = false
            }
        )
    }
}

