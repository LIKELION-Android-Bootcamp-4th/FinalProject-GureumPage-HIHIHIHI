package com.hihihihi.gureumpage.ui.mypage.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun MyPageUserProfileCard(
    modifier: Modifier = Modifier,
    title: String,
    badge: String, // 칭호
    nickname: String, // 유저 닉네임
    totalPages: String,
    totalBooks: String,
    totalTime: String,
    onEditNicknameClick: () -> Unit = {} // 연필 아이콘 클릭 시 콜백
) {
    val colors = GureumTheme.colors
    val typography = GureumTypography

    GureumCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        corner = 12.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    //인사
                    Text(
                        text = title,
                        style = typography.titleLarge,
                        color = colors.gray800
                    )
                    Spacer(Modifier.height(2.dp))

                    //칭호 + 닉네임
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //칭호
                        Text(
                            text = badge,
                            style = typography.titleLarge,
                            color = colors.point,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        //닉네임
                        Text(
                            text = nickname,
                            style = typography.titleLarge,
                            color = colors.gray800,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onEditNicknameClick() }, // 다이얼로그 열기
                    contentAlignment = Alignment.Center
                ) {
                    //연필 아이콘
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pen_outline),
                        contentDescription = "프로필 수정",
                        tint = colors.gray400,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            //구분선
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = colors.dividerDeep, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            //독서 통계
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatColumn("총 페이지", totalPages)
                StatColumn("총 권수", totalBooks)
                StatColumn("총 독서 시간", totalTime)
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    val colors = GureumTheme.colors
    val typography = GureumTypography

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = typography.bodySmall, color = colors.gray500)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, style = typography.bodyMedium, color = colors.gray800)
    }
}