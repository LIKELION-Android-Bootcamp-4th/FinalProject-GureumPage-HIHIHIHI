package com.hihihihi.gureumpage.ui.mypage.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

@Composable
fun MyPageMenuSettingItem(
    title: String,
    showSwitch: Boolean = false, // 스위치 노출 여부
    showArrow: Boolean = true,
    switchChecked: Boolean = false, // 스위치 on/off 상태
    onSwitchToggle: (GureumThemeType) -> Unit = {}, //스위치 변경 시 콜백
    textColor: Color = GureumTheme.colors.gray700,
    minFeedbackMs: Long = 120L,
    onClick: () -> Unit, //항목 클릭 시 액션
) {
    val colors = GureumTheme.colors
    val typography = GureumTypography

    var pressed by remember { mutableStateOf(false) }

    Surface(
        // Surface는 컨테이너만 담당 (onClick 주지 않음 = 기본 리플 차단)
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .heightIn(min = 56.dp) // 접근성 여유
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // 눌림 시작
                        var consumed = false
                        val elapsed = measureTimeMillis {
                            // 손가락을 뗄 때까지 대기(취소/이동 포함)
                            consumed = tryAwaitRelease()
                        }
                        // 아주 빠른 탭도 최소 표시 시간 보장
                        if (elapsed < minFeedbackMs) delay(minFeedbackMs - elapsed)
                        if (consumed) onClick()
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = typography.titleMedium, color = textColor)

            if (showSwitch) {
                Switch(
                    checked = switchChecked,
                    onCheckedChange = {
                        onSwitchToggle(if (it) GureumThemeType.DARK else GureumThemeType.LIGHT)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.primary,
                        checkedTrackColor = colors.primary50,
                        uncheckedTrackColor = colors.gray300,
                        uncheckedThumbColor = colors.gray150
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }

            if (showArrow) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = colors.gray300
                )
            }
        }
    }
}
