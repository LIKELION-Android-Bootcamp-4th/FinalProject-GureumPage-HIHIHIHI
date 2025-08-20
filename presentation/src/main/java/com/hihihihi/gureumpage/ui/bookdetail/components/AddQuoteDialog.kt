package com.hihihihi.gureumpage.ui.bookdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Dialog
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.GureumTextField
import com.hihihihi.gureumpage.designsystem.components.Medi10Text
import com.hihihihi.gureumpage.designsystem.components.Medi12Text
import com.hihihihi.gureumpage.designsystem.components.Semi12Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun AddQuoteDialog(
    lastPage: Int?,
    onDismiss: () -> Unit,
    onSave: (page: String?, content: String) -> Unit,
) {
    var pageNumber by remember { mutableStateOf("") }
    var quote by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        GureumCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
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
                        Semi16Text(text = "필사 추가", color = GureumTheme.colors.gray900)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "닫기"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Semi12Text(
                    "페이지 번호(선택사항)",
                    modifier = Modifier.padding(start = 4.dp),
                    color = GureumTheme.colors.gray800
                )
                Spacer(modifier = Modifier.height(6.dp))
                GureumTextField(
                    value = pageNumber,
                    onValueChange = {
                        pageNumber = it.filter(Char::isDigit)
                            .toIntOrNull()
                            ?.coerceAtMost(lastPage ?: Int.MAX_VALUE)
                            ?.toString()
                            ?: ""
                    },
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


                // 인상 깊은 문장
                Text(
                    buildAnnotatedString {
                        withStyle(
                            GureumTypography.titleSmall.toSpanStyle()
                                .copy(color = GureumTheme.colors.gray800)
                        ) { append("인상 깊은 문장을 적어보세요") }
                        withStyle(
                            GureumTypography.titleSmall.toSpanStyle()
                                .copy(color = GureumTheme.colors.primary)
                        ) { append(" *") }
                    }, modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                GureumTextField(
                    value = quote,
                    onValueChange = { quote = it },
                    modifier = Modifier.fillMaxWidth(),
                    hint = "책에서 마음에 드는 문장이나 생각을 자유롭게 적어보세요.",
                    minLines = 6,
                    maxLines = 6,
                    imeAction = ImeAction.Done
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Tip 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 0.5.dp,
                            color = GureumTheme.colors.gray200,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tooltip),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = GureumTheme.colors.systemGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Medi12Text(
                            text = "팁",
                            color = GureumTheme.colors.gray800
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Medi10Text(
                            text = "필사는 나만의 독서 기록을 남기는 좋은 방법입니다.\n마음에 드는 문장이나 떠오른 생각을 자유롭게 적어보세요.",
                            color = GureumTheme.colors.gray400
                        )
                    }
                }


                Spacer(modifier = Modifier.height(20.dp))

                // 저장 버튼
                GureumButton(
                    text = "저장하기",
                    enabled = quote.isNotBlank(),
                    onClick = {
                        onSave(pageNumber.ifBlank { null }, quote)
                        onDismiss()
                    }
                )
            }
        }
    }
}
