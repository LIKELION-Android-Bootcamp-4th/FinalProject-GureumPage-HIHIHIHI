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
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun MyPageMenuSettingItem(
    title: String,
    showSwitch: Boolean = false,
    onClick: () -> Unit
) {
    val colors = GureumTheme.colors
    val typography = GureumTypography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() }, // TODO: 기능 구현 (화면이동,다크모드)
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = typography.titleMedium, color = colors.gray700)
        if(showSwitch) {
            Switch(
                checked = false,
                onCheckedChange = {}, // TODO: 다크모드 상태
                colors = SwitchDefaults.colors(checkedThumbColor = colors.primary)
            )
        } else {
            //화살표 아이콘 (오른쪽 화살표가 없어서 추후 생기면 변경)
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.gray300
            )
        }
    }
}
