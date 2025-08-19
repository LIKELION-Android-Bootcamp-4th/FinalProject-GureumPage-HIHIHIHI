package com.hihihihi.gureumpage.ui.login.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun SocialLoginButton(
    text: String,
    textColor: Color,
    iconResId: Int,
    backgroundColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 텍스트는 정중앙
            Text(
                text = text,
                style = GureumTypography.titleLarge,
                color = textColor,
                modifier = Modifier.align(Alignment.Center)
            )

            // 아이콘은 좌측 끝 (padding 고려해서 정렬)
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(16.dp),
                tint = Color.Unspecified
            )
        }
    }
}

@Preview
@Composable
private fun PreviewButton() {
    SocialLoginButton(
        text = "카카오 로그인",
        textColor = Color.Black,
        iconResId = R.drawable.ic_kakao,
        backgroundColor = Color(0xFFFEE500), // Kakao Yellow
        onClick = {}
    )
}
