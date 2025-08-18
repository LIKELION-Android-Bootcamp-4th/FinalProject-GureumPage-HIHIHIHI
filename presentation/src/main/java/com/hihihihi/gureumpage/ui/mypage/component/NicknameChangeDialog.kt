package com.hihihihi.gureumpage.ui.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun NicknameChangeDialog(
    currentNickname: String,
    onDismiss: () -> Unit, // 닫기 콜백
    onSave: (String) -> Unit, // 저장 콜백
) {
    val colors = GureumTheme.colors
    val typo = GureumTypography


    var text by remember { mutableStateOf(TextFieldValue("")) } // 처음 진입 시 빈 값
    val rule = remember(text) { validateNickname(text.text) } // 긴단 검증
    val canSave = rule is NicknameRule.Ok // 저장 가능 여부

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // 기본 크기가 작아서 폭 해제
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth() //가로 크게
                .padding(horizontal = 20.dp) // 좌우 여백
                .widthIn(max = 560.dp)
                .shadow(18.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = colors.gray0
        ) {
            Column(
                modifier = Modifier
                    .background(colors.gray0)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .heightIn(min = 260.dp), // 최소 높이
                verticalArrangement = Arrangement.Top
            ) {
                // 타이틀 + 닫기
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "닉네임 변경",
                        style = typo.headlineSmall,
                        color = colors.gray800,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "닫기",
                        tint = colors.gray400,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // 안내 문구
                Text(
                    text = "변경할 닉네임을 입력해주세요",
                    style = typo.bodySmall,
                    color = colors.gray400,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // 입력 필드
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (text.text.isNotEmpty()) {
                            IconButton(onClick = { text = TextFieldValue("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "지우기",
                                    tint = colors.gray300
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    textStyle = typo.bodyMedium.copy(color = colors.gray800),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.gray0,
                        unfocusedContainerColor = colors.gray0,
                        disabledContainerColor = colors.gray0,
                        focusedIndicatorColor = colors.textFieldOutline,
                        unfocusedIndicatorColor = colors.textFieldOutline,
                        cursorColor = colors.gray600,
                        focusedTextColor = colors.gray800,
                        unfocusedTextColor = colors.gray800,
                        focusedPlaceholderColor = colors.gray300,
                        unfocusedPlaceholderColor = colors.gray300,
                    ),
                    //필드 안 힌트
                    placeholder = {
                        Text(
                            "닉네임으로 나만의 개성을 표현해 보세요",
                            style = typo.bodyMedium,
                            color = colors.gray300
                        )
                    },

                    isError = rule is NicknameRule.Length ||
                            rule is NicknameRule.IllegalChar ||
                            rule is NicknameRule.ForbiddenWord
                )

                Spacer(Modifier.height(8.dp))

                // 검증 메세지 (rule 상태에 따라 색상/문구 변경)
                val (msg, msgColor) = when (rule) {
                    is NicknameRule.Empty -> "2~8글자 이내로 설정해주세요." to colors.gray400
                    is NicknameRule.Length -> "2~8글자 이내로 설정해주세요." to colors.systemRed
                    is NicknameRule.IllegalChar -> "특수문자나 공백은 사용할 수 없어요." to colors.systemRed
                    is NicknameRule.ForbiddenWord -> "부적절한 단어는 포함할 수 없어요." to colors.systemRed
                    is NicknameRule.Ok -> "사용 가능한 닉네임입니다." to colors.systemGreen
                }
                Text(
                    text = msg,
                    style = typo.bodySmall,
                    color = msgColor,
                    modifier = Modifier.fillMaxWidth()

                )

                Spacer(Modifier.height(36.dp))

                // 저장 버튼
                GureumButton(
                    text = "저장하기",
                    enabled = canSave,
                    onClick = { onSave(text.text) }
                )
            }
        }
    }
}

// ===== 검증 로직 =====
private val forbiddenWords = listOf("운영자", "관리자", "admin", "시발", "shit", "fuck", "병신") // 필요시 확장
private val nicknameRegex = Regex("^[가-힣a-zA-Z0-9]{2,8}$")

private fun validateNickname(text: String): NicknameRule {
    if (text.isBlank()) return NicknameRule.Empty
    if (!nicknameRegex.matches(text)) {
        if (text.length !in 2..8) return NicknameRule.Length
        return NicknameRule.IllegalChar
    }
    if (forbiddenWords.any { text.contains(it, ignoreCase = true) }) {
        return NicknameRule.ForbiddenWord
    }
    return NicknameRule.Ok
}

private sealed interface NicknameRule {
    data object Empty : NicknameRule
    data object Length : NicknameRule
    data object IllegalChar : NicknameRule
    data object ForbiddenWord : NicknameRule
    data object Ok : NicknameRule
}