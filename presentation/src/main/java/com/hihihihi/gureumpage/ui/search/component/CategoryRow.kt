package com.hihihihi.gureumpage.ui.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun CategoryRow(
    title: String, subtitle: String, isSelected: Boolean, onClick: () -> Unit
) {
    val backgroundColor =
        if (isSelected) GureumTheme.colors.primary50 else GureumTheme.colors.background10
    val borderColor =
        if (isSelected) GureumTheme.colors.primary50 else GureumTheme.colors.background50

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isSelected) GureumTheme.colors.primary else Color.Transparent)
                .border(
                    1.5.dp,
                    if (isSelected) Color.Transparent else GureumTheme.colors.gray500,
                    shape = CircleShape
                )
        ) {
            if (isSelected) {
                Icon(
                    //TODO 이미지 리소스 추가 필요
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "선택 됨",
                    tint = GureumTheme.colors.white,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center),
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        // 텍스트
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) GureumTheme.colors.gray700 else GureumTheme.colors.gray500,
                fontSize = 14.sp
            )
            Text(text = subtitle, color = GureumTheme.colors.gray500, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))

        // 아이콘
        //TODO 다른 아이콘 리소스 추가 필요
        Icon(
            painter = painterResource(id = if(title == "읽는 중") R.drawable.ic_cloud_reading else R.drawable.ic_launcher_background),
            contentDescription = title,
            modifier = Modifier.size(40.dp),
            tint = Color.Unspecified
        )
    }
}
