package com.hihihihi.gureumpage.ui.timer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.Semi12Text

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FloatingTimer(
    timerState: TimerSharedState,
    isMinimized: Boolean,
    isOnRightSide: Boolean,
    onToggleTimer: () -> Unit,
    onOpenQuoteDialog: () -> Unit,
    onMinimize: () -> Unit,
    onExpand: () -> Unit
) {
    val primaryColor = Color(0xFFFFCB5B)
    val cardColor = Color(0xFF2E333A)
    val gray700Color = Color(0xFFD1DBE7)

    val displayTime = remember(timerState.elapsedSec) {
        "%02d:%02d".format(timerState.elapsedSec / 60, timerState.elapsedSec % 60)
    }

    AnimatedContent(
        targetState = isMinimized,
        transitionSpec = {
            if (targetState) {
                (slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth / 2 },
                    animationSpec = tween(300)
                ) + fadeIn()).togetherWith(
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth / 2 },
                        animationSpec = tween(300)
                    ) + fadeOut()
                )
            } else {
                (slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 2 },
                    animationSpec = tween(300)
                ) + fadeIn()).togetherWith(
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth / 2 },
                        animationSpec = tween(300)
                    ) + fadeOut()
                )
            }
        }
    ) { minimized ->
        if (minimized) {
            // 최소화 상태
            Box(
                modifier = Modifier
                    .background(
                        cardColor,
                        RoundedCornerShape(if (isOnRightSide) 12.dp else 12.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                    .clickable { onExpand() }
            ) {
                Semi12Text(
                    displayTime,
                    color = primaryColor
                )
            }
        } else {
            // 확장 상태
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .background(cardColor, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row {

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        timerState.bookInfo?.title?.let {
                            Semi12Text(
                                it,
                                color = gray700Color,
                                maxLine = 1,

                                )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Semi12Text(
                            displayTime,
                            color = primaryColor
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 버튼들
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onOpenQuoteDialog,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_lightbulb_filled),
                                tint = gray700Color,
                                contentDescription = "필사 추가"
                            )
                        }

                        IconButton(
                            onClick = onToggleTimer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (timerState.isRunning) R.drawable.ic_pause
                                    else R.drawable.ic_play
                                ),
                                tint = primaryColor,
                                contentDescription = "타이머 시작/정지"
                            )
                        }
                    }
                }
            }
        }
    }
}