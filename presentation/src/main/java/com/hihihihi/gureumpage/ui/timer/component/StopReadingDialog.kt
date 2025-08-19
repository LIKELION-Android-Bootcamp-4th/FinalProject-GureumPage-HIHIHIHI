package com.hihihihi.gureumpage.ui.timer.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.components.GureumTextField
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun StopReadingDialog(
    displayTime: String,
    title: String,
    author: String,
    currentPage: Int? = null,
    totalPage: Int? = null,
    willSave: Boolean = true,
    onConfirmStop: () -> Unit,
    onDismiss: () -> Unit,
    onConfirmStopPages: (startPage: Int, endPage: Int) -> Unit = { _, _ -> onConfirmStop()},
) {
    val colors = GureumTheme.colors
    val typo = GureumTypography

//    val corner = 16.dp
//    val hPad = 24.dp

    var startPage by remember(currentPage) { mutableStateOf((currentPage ?: 0).takeIf { it > 0 }?.toString() ?: "") }
    var endPage by remember { mutableStateOf("") }

    fun String.onlyDigits(): String = filter(Char::isDigit)
    fun safeInt(s: String?): Int = s?.toIntOrNull() ?: -1

    val start = safeInt(startPage)
    val end = safeInt(endPage)
    val isPageEntered = start >= 0 && end >= 0
    val isPageOrdered = if (isPageEntered) start <= end else true
    val isPageInRange = if (totalPage != null && totalPage > 0 && isPageEntered)
        (start in 0..totalPage) && (end in 0..totalPage) else true

    val canConfirm = isPageOrdered && isPageInRange


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .widthIn(max = 560.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = GureumTheme.background.tonalElevation,
            border = BorderStroke(1.dp, colors.dividerDeep),
            color = colors.card,
            contentColor = colors.gray800
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally // ★ 전체 가운데 정렬
            ) {
                // 제목 + 닫기
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        text = "독서를 종료하시겠습니까?",
                        style = typo.headlineSmall,
                        textAlign = TextAlign.Center,              // ★ 가운데
                        modifier = Modifier
                            .fillMaxWidth()                        // ★ 폭을 채워야 중앙 정렬이 적용됨
                            .align(Alignment.Center)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "닫기",
                            tint = colors.gray500
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (willSave) "현재까지의 독서 기록이 저장됩니다."
                    else "현재까지의 독서 시간 기록이 저장되지 않습니다.",
                    style = typo.bodySmall,
                    textAlign = TextAlign.Center,                 // ★ 가운데
                    modifier = Modifier.fillMaxWidth(),           // ★
                    color = colors.gray500
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "오늘의 독서 시간",
                    style = typo.bodySmall,
                    textAlign = TextAlign.Center,                 // ★ 가운데
                    modifier = Modifier.fillMaxWidth(),           // ★
                    color = colors.gray400
                )

                Text(
                    text = displayTime,
                    style = typo.headlineLarge,
                    color = colors.primaryDeep,
                    textAlign = TextAlign.Center,                 // ★ 가운데
                    modifier = Modifier
                        .fillMaxWidth()                           // ★
                        .padding(top = 2.dp, bottom = 10.dp)
                )

                // 책 정보 카드: 카드 안의 텍스트는 좌측 정렬 유지(디자인 기준)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.card,
                    border = BorderStroke(1.dp, colors.dividerDeep),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = title,
                            style = typo.bodyMedium,
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

                // ---------- 페이지 입력 ----------
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "페이지",
                    style = typo.bodySmall,
                    color = colors.gray500,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GureumTextField(
                        value = startPage,
                        onValueChange = { startPage = it.onlyDigits() },
                        hint = "시작 페이지",
                        modifier = Modifier.weight(1f)
                    )
                    Text("~", style = typo.bodyMedium, color = colors.gray700)
                    GureumTextField(
                        value = endPage,
                        onValueChange = { endPage = it.onlyDigits() },
                        hint = "끝 페이지",
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Done
                    )
                }

                if (!isPageOrdered || !isPageInRange) {
                    Spacer(Modifier.height(6.dp))
                    val warn = when {
                        !isPageOrdered -> "시작 페이지가 끝 페이지보다 작거나 같아야 해요."
                        !isPageInRange && totalPage != null -> "0 ~ $totalPage 범위로 입력해 주세요."
                        else -> ""
                    }
                    if (warn.isNotEmpty()) {
                        Text(
                            text = warn,
                            style = typo.bodySmall,
                            color = colors.systemRed,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                GureumButton(
                    text = "종료하기",
                    onClick = {
                        if (canConfirm) onConfirmStopPages(start, end)
                    },
                    enabled = canConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        }
    }
}