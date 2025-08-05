package com.hihihihi.gureumpage.ui.home.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.Floating
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.home.HomeScreenContent
import com.hihihihi.gureumpage.ui.home.mock.mockUser

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SearchBarWithBackground() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),


    ) {
        val maxWidth: Dp = maxWidth
        val maxHeight: Dp = maxHeight
        Image(
            //TODO 이미지 테마에 맞춰 변경되어야 함 현재는 Light BG가 없음
            painter = painterResource(id = R.drawable.bg_home_dark),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp), // 배경이 겹쳐질 만큼만 높이
            contentScale = ContentScale.Crop
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            //TODO 현재는 진짜 TextField 인데 누르면 Search Screen으로 가도록 해야함
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("책 검색") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_outline),
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            GureumTypography.titleLarge.toSpanStyle()
                                .copy(color = GureumTheme.colors.primary)
                        ) { append(mockUser.appellation) }
                        withStyle(
                            GureumTypography.titleLarge.toSpanStyle()
                                .copy(color = GureumTheme.colors.gray900)
                        ) {
                            append(" ${mockUser.nickName}")

                        }
                        withStyle(
                            GureumTypography.bodyLarge.toSpanStyle()
                                .copy(color = GureumTheme.colors.gray900)
                        ) { append(" 님") }
                    })

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            GureumTypography.bodyLarge.toSpanStyle()
                                .copy(color = GureumTheme.colors.gray900)
                        ) { append("오늘도 ") }
                        withStyle(
                            GureumTypography.titleLarge.toSpanStyle()
                                .copy(color = GureumTheme.colors.primary)
                        ) { append("구름한장") }
                        withStyle(
                            GureumTypography.bodyLarge.toSpanStyle()
                                .copy(color = GureumTheme.colors.gray900)
                        ) { append("과 함께") }
                    }
                )

                // \n 으로 했더니 간격이 이상해서 별도로 뺌
                Text(
                    text = "마음의 양식을 쌓아볼까요?",
                    style = MaterialTheme.typography.bodyLarge.copy(color = GureumTheme.colors.gray900)
                )

            }
        }

        Floating(
            modifier = Modifier
                .offset(
                    x = maxWidth * 0.6f, // 오른쪽 비율 위치
                    y = maxHeight * 0.38f
                )  // 아래쪽에서 살짝 위로) // 오른쪽으로 좀 당기고 아래로 살짝 내림
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cloud_reading),
                contentDescription = "Floating Cloud",
                modifier = Modifier.size(130.dp)
            )
        }
    }

}


@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HomePreview() {
    val fakeNavController = rememberNavController()

    GureumPageTheme {
        SearchBarWithBackground()
    }
}
