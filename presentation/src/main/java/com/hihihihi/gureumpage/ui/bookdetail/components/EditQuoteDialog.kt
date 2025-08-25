package com.hihihihi.gureumpage.ui.bookdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.*
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography


@Composable
fun EditQuoteDialog(
    initialContent: String,
    initialPageNumber: Int?,
    onDismiss: () -> Unit,
    onSave: (String, Int?) -> Unit
) {
    var pageNumber by remember { mutableStateOf(initialPageNumber?.toString() ?: "") }
    var quote by remember { mutableStateOf(initialContent) }
    val maxLen = 500

    Dialog(onDismissRequest = onDismiss) {
        GureumCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = GureumTheme.colors.primary50,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lightbulb_outline),
                                contentDescription = null,
                                tint = GureumTheme.colors.primary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Semi16Text(text = "필사 수정", color = GureumTheme.colors.gray900)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "닫기"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 페이지 번호
                Semi12Text("페이지 번호(선택사항)", modifier = Modifier.padding(start = 4.dp), color = GureumTheme.colors.gray800)
                Spacer(modifier = Modifier.height(6.dp))
                GureumTextField(
                    value = pageNumber,
                    onValueChange = { pageNumber = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    hint = "예: 157",
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_book_outline),
                            contentDescription = null,
                            tint = GureumTheme.colors.gray300,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    keyboardType = KeyboardType.Number,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 본문 라벨(필수 * 표시)
                Medi12Text(
                    text = buildAnnotatedString {
                        withStyle(GureumTypography.titleSmall.toSpanStyle().copy(color = GureumTheme.colors.gray800)) {
                            append("인상 깊은 문장을 적어보세요")
                        }
                        withStyle(GureumTypography.titleSmall.toSpanStyle().copy(color = GureumTheme.colors.primary)) {
                            append(" *")
                        }
                    }.text,
                    color = GureumTheme.colors.gray800,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))

                // 본문 입력
                GureumTextField(
                    value = quote,
                    onValueChange = { input ->
                        quote = if (input.length <= maxLen) input else input.take(maxLen)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    hint = "책에서 마음에 드는 문장이나 생각을 자유롭게 적어보세요.",
                    minLines = 6,
                    maxLines = 6,
                    imeAction = ImeAction.Done
                )

                // 글자 수 카운터 (우측 하단)
                Box(modifier = Modifier.fillMaxWidth()) {
                    Medi10Text(
                        text = "최대 ${maxLen}자",
                        color = GureumTheme.colors.gray400,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                // 저장 버튼
                GureumButton(
                    text = "저장하기",
                    enabled = quote.isNotBlank(),
                    onClick = {
                        onSave(quote.trim(), pageNumber.ifBlank { null }?.toInt())
                        onDismiss()
                    }
                )
            }
        }
    }
}