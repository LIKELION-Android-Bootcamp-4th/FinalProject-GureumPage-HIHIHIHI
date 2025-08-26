package com.hihihihi.gureumpage.ui.mypage.component

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hihihihi.gureumpage.common.utils.NicknameRule
import com.hihihihi.gureumpage.common.utils.NicknameValidator.validate
import com.hihihihi.gureumpage.common.utils.NicknameValidator.validateNickname
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.components.GureumCard
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

    var nickname by remember { mutableStateOf("") } // 처음 진입 시 빈 값
    val rule = validate(nickname)  // 긴단 검증

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // 기본 크기가 작아서 폭 해제
    ) {
        GureumCard(
            modifier = Modifier
                .fillMaxWidth() //가로 크게
                .padding(horizontal = 20.dp)
                .widthIn(max = 560.dp)
                .shadow(18.dp, RoundedCornerShape(16.dp)),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .padding(bottom = 12.dp)
                    .heightIn(min = 200.dp), // 최소 높이
                verticalArrangement = Arrangement.Top
            ) {
                // 타이틀 + 닫기
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("닉네임 변경", style = typo.headlineSmall, color = colors.gray800)
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "닫기",
                        tint = colors.gray400,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(Modifier.height(20.dp))

                // 안내? 문구
                Text(
                    text = "변경할 닉네임을 입력해주세요",
                    style = typo.bodySmall,
                    color = colors.gray800
                )

                Spacer(Modifier.height(12.dp))

                // 입력 필드
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (nickname.isNotEmpty()) {
                            IconButton(onClick = { nickname = "" }) {
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
                        .heightIn(min = 56.dp), //2줄 힌트 대비
                    textStyle = typo.bodyMedium.copy(color = colors.gray800),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.card,
                        unfocusedContainerColor = colors.card,
                        disabledContainerColor = colors.card,

                        focusedIndicatorColor = colors.textFieldOutline,   // 아웃라인 색
                        unfocusedIndicatorColor = colors.textFieldOutline,

                        cursorColor = colors.gray600,

                        focusedTextColor = colors.gray800,
                        unfocusedTextColor = colors.gray800,

                        focusedPlaceholderColor = colors.gray300,
                        unfocusedPlaceholderColor = colors.gray300,
                    ),
                    //필드 안 힌트
                    placeholder = {
                        Text("닉네임으로 나만의 개성을 표현해 보세요", style = typo.bodyMedium, color = colors.gray300)
                    },
                    isError = rule !is NicknameRule.Ok
                )

                Spacer(Modifier.height(12.dp))

                // 검증 메세지 (rule 상태에 따라 색상/문구 변경)
                val (msg, msgColor) = when (rule) {
                    NicknameRule.Empty -> "2~8글자 이내로 설정해주세요." to GureumTheme.colors.gray400
                    NicknameRule.Length -> "2~8글자 이내로 설정해주세요." to GureumTheme.colors.systemRed
                    NicknameRule.Equal -> "이전 닉네임과 동일합니다." to GureumTheme.colors.systemRed
                    NicknameRule.InnerSpace -> "글자 사이 공백은 사용할 수 없어요." to GureumTheme.colors.systemRed
                    NicknameRule.IllegalChar -> "한글/영문/숫자만 사용할 수 있어요." to GureumTheme.colors.systemRed
                    NicknameRule.ForbiddenWord -> "부적절한 단어는 포함할 수 없어요." to GureumTheme.colors.systemRed
                    NicknameRule.Reserved -> "사용할 수 없는 닉네임이에요." to GureumTheme.colors.systemRed
                    NicknameRule.Ok -> "사용 가능한 닉네임입니다." to GureumTheme.colors.systemGreen
                }

                Text(text = msg, style = typo.bodySmall, color = msgColor)

                Spacer(Modifier.height(18.dp))

                // 저장 버튼
                GureumButton(
                    text = "저장하기",
                    enabled = nickname.validateNickname(),
                    onClick = { onSave(nickname.trim()) }
                )
            }
        }
    }
}
