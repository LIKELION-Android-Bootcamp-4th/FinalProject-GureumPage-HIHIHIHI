package com.hihihihi.gureumpage.ui.timer.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.components.GureumCancelButton
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun StopReadingConfirmDialog(
    displayTime: String,
    title: String,
    author: String,
    willSave: Boolean = false, // 뒤로가기는 미저장 안내
    onContinue: () -> Unit,    // 계속 읽기
    onStop: () -> Unit,        // 그만 읽기
    onDismiss: () -> Unit = onContinue, // 바깥 터치/뒤로가기는 기본 '계속 읽기'
) {
    val colors = GureumTheme.colors
    val typo = GureumTypography

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // 넓이 제어를 위해
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .widthIn(max = 560.dp),
            shape = RoundedCornerShape(16.dp),
            color = colors.card,
            contentColor = colors.gray800,
            tonalElevation = GureumTheme.background.tonalElevation,
            border = BorderStroke(1.dp, colors.dividerDeep)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "독서를 종료하시겠습니까?",
                    style = typo.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (willSave) "현재까지의 독서 기록이 저장됩니다."
                    else "현재까지의 독서 시간 기록이 저장되지 않습니다.",
                    style = typo.bodySmall,
                    color = if (willSave) colors.gray500 else colors.systemRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "오늘의 독서 시간",
                    style = typo.bodySmall,
                    color = colors.gray400,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = displayTime,
                    style = typo.headlineLarge,
                    color = colors.primaryDeep,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 10.dp)
                )

                // 책 정보 카드(알약)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.card,
                    border = BorderStroke(1.dp, colors.dividerDeep),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text(
                            text = title,
                            style = typo.bodyMedium,
                            color = colors.gray800,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = author,
                            style = typo.bodySmall,
                            color = colors.gray500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GureumCancelButton(
                        text = "그만 읽기",
                        onClick = onStop,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )
                    GureumButton(
                        text = "계속 읽기",
                        onClick = onContinue,
                        enabled = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )
                }
            }
        }
    }
}