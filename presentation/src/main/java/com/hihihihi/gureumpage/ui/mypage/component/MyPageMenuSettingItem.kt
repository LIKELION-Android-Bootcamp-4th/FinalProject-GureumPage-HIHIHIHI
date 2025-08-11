package com.hihihihi.gureumpage.ui.mypage.component


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun MyPageMenuSettingItem(
    title: String,
    showSwitch: Boolean = false, // 스위치 노출 여부
    switchChecked: Boolean = false, // 스위치 on/off 상태
    onSwitchToggle: (Boolean) -> Unit = {}, //스위치 변경 시 콜백
    textColor: Color = GureumTheme.colors.gray700,
    onClick: () -> Unit //항목 클릭 시 액션
) {
    val colors = GureumTheme.colors
    val typography = GureumTypography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() }, // TODO: 기능 구현 (화면이동)
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = typography.titleMedium, color = textColor)
        if(showSwitch) {
            //다크모드 토글 스위치
            Switch(
                checked = switchChecked,
                onCheckedChange = onSwitchToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.primary,
                    checkedTrackColor = colors.primary50,
                    uncheckedTrackColor = colors.gray300,
                    uncheckedThumbColor = colors.gray150
                )
            )
        } else {
            // 스위치가 아닌 경우 아이콘
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = colors.gray300
            )
        }
    }
}
