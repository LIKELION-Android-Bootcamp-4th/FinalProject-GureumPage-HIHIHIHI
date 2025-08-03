package com.hihihihi.gureumpage.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.component.Floating
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.home.mock.mockUser

@Composable
fun SearchBarWithBackground() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .paint(
                // TODO 테마에 따라 배경도 바꿔줘야 함 아직 right 배경 안나옴
                painterResource(id = R.drawable.bg_home_dark),
                contentScale = ContentScale.Crop
            )
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
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
                Row {
                    Text(
                        text = mockUser.appellation,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GureumTheme.colors.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = mockUser.nickName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = GureumTheme.colors.gray900
                        )
                    )
                    Text(
                        text = " 님",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = GureumTheme.colors.gray900
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row {
                    Text(
                        text = "오늘도",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = GureumTheme.colors.gray900
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "구름책방",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = GureumTheme.colors.primary
                        )
                    )
                    Text(
                        text = "과 함께",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = GureumTheme.colors.gray900
                        )
                    )
                }

                Text(
                    text = "마음의 양식을 쌓아볼까요?",
                    style = MaterialTheme.typography.bodyLarge.copy(color = GureumTheme.colors.gray900)
                )

            }
        }

        Floating(
            modifier = Modifier
                .align(Alignment.BottomEnd) // 박스 기준 오른쪽 위
                .offset(x = (-16).dp, y = (-35).dp) // 오른쪽으로 좀 당기고 아래로 살짝 내림
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cloud_reading),
                contentDescription = "Floating Cloud",
                modifier = Modifier.size(130.dp)
            )
        }

    }
}
