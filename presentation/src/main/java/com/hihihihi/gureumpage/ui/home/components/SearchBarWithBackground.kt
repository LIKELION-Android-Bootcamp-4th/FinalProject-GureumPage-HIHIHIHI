package com.hihihihi.gureumpage.ui.home.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.hihihihi.domain.model.User
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.Floating
import com.hihihihi.gureumpage.designsystem.components.Medi12Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.home.mock.mockUser

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SearchBarWithBackground(
    user: User,
    onSearchBarClick: () -> Unit = {}
) {
    val backGroundImage = if(GureumTheme.isDarkTheme) R.drawable.bg_home_dark else R.drawable.bg_home_light

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
    ) {
        val maxWidth: Dp = maxWidth
        val maxHeight: Dp = maxHeight
        Image(
            painter = painterResource(id = backGroundImage),
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(GureumTheme.colors.searchTextField, shape = RoundedCornerShape(50))
                    .border(1.dp, GureumTheme.colors.textFieldOutline, shape = RoundedCornerShape(50)) // 테두리
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onSearchBarClick() }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_outline),
                        contentDescription = null,
                        tint = GureumTheme.colors.gray400
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Medi12Text(
                        text = "책 검색",
                        color = GureumTheme.colors.gray400
                    )
                }
            }


            Spacer(modifier = Modifier.height(24.dp))
            Column {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            GureumTypography.titleLarge.toSpanStyle()
                                .copy(color = GureumTheme.colors.primary)
                        ) { append(user.appellation) }
                        withStyle(
                            GureumTypography.titleLarge.toSpanStyle()
                                .copy(color = GureumTheme.colors.gray900)
                        ) {
                            append(" ${user.nickname}")

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
                    y = maxHeight * 0.3f // 아래 비율 위치
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cloud_reading),
                contentDescription = "Floating Cloud",
                modifier = Modifier.size(130.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .align(Alignment.BottomCenter)
                .background(
                    color = GureumTheme.colors.background,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
        )
    }

}


@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HomePreview() {
    val fakeNavController = rememberNavController()

    GureumPageTheme {
        SearchBarWithBackground(mockUser)
    }
}
